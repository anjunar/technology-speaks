package $package$

import com.anjunar.scala.mapper.annotations.{DoNotLoad, JsonSchema, NoValidation, SecuredOwner}
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jpa.Pair
import com.anjunar.technologyspeaks.media.Media
import com.anjunar.technologyspeaks.openstreetmap.geocoding.GeoService
import com.anjunar.technologyspeaks.security.{Authenticator, EmailCredential, Secured}
import com.anjunar.technologyspeaks.shared.$naming$Schema
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


@Path("$path$")
@ApplicationScoped
@Secured
class $naming$FormResource extends SchemaBuilderContext {

  @Inject
  var authenticator: Authenticator = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[$naming$FormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Create", linkType = LinkType.FORM)
  def create: $naming$ = {
    val entity = new $naming$

    forLinks(classOf[$naming$], (entity, link) => {
      linkTo(methodOn(classOf[$naming$FormResource]).save(entity))
        .build(link.addLink)
      linkTo(methodOn(classOf[$naming$FormResource]).delete(entity.id))
        .build(link.addLink)
    })

    entity
  }

  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[$naming$FormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Profile", linkType = LinkType.FORM)
  def read(@PathParam("id") entity: $naming$): $naming$ = {

    forLinks(classOf[$naming$], (entity, link) => {
      linkTo(methodOn(classOf[$naming$FormResource]).save(entity))
        .build(link.addLink)
      linkTo(methodOn(classOf[$naming$FormResource]).delete(entity.id))
        .build(link.addLink)
    })

    entity
  }

  @POST
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(classOf[$naming$FormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Save", linkType = LinkType.FORM)
  def save(@JsonSchema(classOf[$naming$FormSchema]) entity: $naming$): $naming$ = {
    entity.persist()

    forLinks(classOf[$naming$], (entity, link) => {
      linkTo(methodOn(classOf[$naming$FormResource]).save(entity))
        .build(link.addLink)
      linkTo(methodOn(classOf[$naming$FormResource]).delete(entity.id))
        .build(link.addLink)
    })


    entity
  }

  @Path("/{id}")
  @DELETE
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Delete", linkType = LinkType.FORM)
  def delete(@PathParam("id") @SecuredOwner entity: $naming$): Response = {

    entity.delete()

    Response.ok().build()
  }
}
