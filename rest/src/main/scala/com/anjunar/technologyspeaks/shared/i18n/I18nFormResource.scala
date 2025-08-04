package com.anjunar.technologyspeaks.shared.i18n

import com.anjunar.scala.mapper.annotations.{DoNotLoad, JsonSchema, NoValidation, PropertyDescriptor, SecuredOwner}
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.scala.universe.ClassPathResolver
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jpa.Pair
import com.anjunar.technologyspeaks.media.Media
import com.anjunar.technologyspeaks.olama.{GenerateRequest, OLlamaService}
import com.anjunar.technologyspeaks.openstreetmap.geocoding.GeoService
import com.anjunar.technologyspeaks.security.{Authenticator, EmailCredential, Secured}
import com.anjunar.technologyspeaks.shared.I18nSchema
import com.typesafe.scalalogging.Logger
import jakarta.annotation.Resource
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.{EntityManager, RollbackException}
import jakarta.security.enterprise.credential.Password
import jakarta.transaction.{Transactional, UserTransaction}
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.{Status, status}
import org.hibernate.exception.ConstraintViolationException

import java.util.{Locale, Objects, UUID}
import scala.compiletime.uninitialized


@Path("/shared/i18ns/i18n")
@ApplicationScoped
@Secured
class I18nFormResource extends SchemaBuilderContext {

  val log: Logger = Logger[I18nFormResource]

  @Inject
  var authenticator: Authenticator = uninitialized

  @Resource
  var transaction : UserTransaction = uninitialized

  @Inject
  var oLlamaService: OLlamaService = uninitialized

  @Inject
  var entityManager : EntityManager = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[I18nFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Create", linkType = LinkType.FORM)
  def create: I18n = {
    val entity = new I18n

    forLinks(classOf[I18n], (entity, link) => {
      linkTo(methodOn(classOf[I18nFormResource]).save(entity))
        .withRel("submit")
        .build(link.addLink)
      linkTo(methodOn(classOf[I18nFormResource]).delete(entity))
        .build(link.addLink)
    })

    entity
  }

  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[I18nFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Profile", linkType = LinkType.FORM)
  def read(@PathParam("id") id: UUID): I18n = {

    val entity = I18n.find(id)

    forLinks(classOf[I18n], (entity, link) => {
      linkTo(methodOn(classOf[I18nFormResource]).save(entity))
        .withRel("submit")
        .build(link.addLink)
      linkTo(methodOn(classOf[I18nFormResource]).delete(entity))
        .build(link.addLink)
    })

    entity
  }

  @POST
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(classOf[I18nFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Save", linkType = LinkType.FORM)
  def save(@JsonSchema(classOf[I18nFormSchema]) entity: I18n): Response = {
    entity.saveOrUpdate()
    
    createRedirectResponse
  }

  @Path("/{id}")
  @DELETE
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Delete", linkType = LinkType.FORM)
  def delete(@PathParam("id") entity: I18n): Response = {

    entity.delete()

    Response.ok().build()
  }

  private def translateWithAI(oLlamaService: OLlamaService, i18n: I18n, locale: Locale) = {
    log.info(s"Generating translation for :${i18n.text} to ${locale.getLanguage}")

    val prompt =
      s"""Translate the following English text into ${locale.getLanguage}.
         |
         |Important:
         |- Preserve all placeholders and formatting symbols exactly as they are.
         |- These include: `{variable}`, `%d`, `%s`, `\n`, `\t`, `:`, `"..."`, etc.
         |- Do not translate anything inside curly braces `{}`, percent signs `%`, or other code-like tokens.
         |""".stripMargin

    val response = oLlamaService.generate(GenerateRequest(system = prompt, prompt = i18n.text))

    val translation = new Translation
    translation.locale = locale
    translation.text = response.trim

    i18n.translations.add(translation)
  }

  def importAnnotations(): Unit = {
    ClassPathResolver.findAnnotation(classOf[PropertyDescriptor])
      .foreach(resolved => {

        transaction.begin()

        var i18n = I18n.find(resolved.annotation.title())

        if (i18n == null) {
          i18n = new I18n
          i18n.text = resolved.annotation.title()
          i18n.saveOrUpdate()
        }

        I18n.languages.filter(locale => i18n.translations.stream().noneMatch(translation => translation.locale == locale))
          .foreach(locale => {

            translateWithAI(oLlamaService, i18n, locale)

          })

        entityManager.flush()
        transaction.commit()

      })

    ClassPathResolver.findAnnotation(classOf[LinkDescription])
      .foreach(resolved => {

        transaction.begin()

        var i18n = I18n.find(resolved.annotation.value())

        if (i18n == null) {
          i18n = new I18n
          i18n.text = resolved.annotation.value()
          i18n.saveOrUpdate()
        }

        I18n.languages.filter(locale => i18n.translations.stream().noneMatch(translation => translation.locale == locale))
          .foreach(locale => {

            translateWithAI(oLlamaService, i18n, locale)

          })

        entityManager.flush()
        transaction.commit()

      })
  }
}
