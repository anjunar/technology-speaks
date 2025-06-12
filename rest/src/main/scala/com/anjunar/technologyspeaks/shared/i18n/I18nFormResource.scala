package com.anjunar.technologyspeaks.shared.i18n

import com.anjunar.scala.mapper.annotations.{DoNotLoad, JsonSchema, NoValidation, SecuredOwner}
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jpa.Pair
import com.anjunar.technologyspeaks.media.Media
import com.anjunar.technologyspeaks.openstreetmap.geocoding.GeoService
import com.anjunar.technologyspeaks.security.{Authenticator, EmailCredential, Secured}
import com.anjunar.technologyspeaks.shared.I18nSchema
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.{EntityManager, RollbackException}
import jakarta.security.enterprise.credential.Password
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.{Status, status}
import org.hibernate.exception.ConstraintViolationException

import java.util.{Objects, UUID}
import scala.compiletime.uninitialized


@Path("/shared/i18ns/i18n")
@ApplicationScoped
@Secured
class I18nFormResource extends SchemaBuilderContext {

  @Inject
  var authenticator: Authenticator = uninitialized

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
}
