package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.{DoNotLoad, JsonSchema, SecuredOwner}
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.{GeoPoint, Role, User}
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jpa.Pair
import com.anjunar.technologyspeaks.media.Media
import com.anjunar.technologyspeaks.openstreetmap.geocoding.GeoService
import com.anjunar.technologyspeaks.security.{Authenticator, EmailCredential, Secured}
import com.anjunar.technologyspeaks.shared.UserSchema
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


@Path("control/users/user")
@ApplicationScoped
@Secured
class UserFormResource extends SchemaBuilderContext {

  @Inject
  var authenticator: Authenticator = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[UserFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Erstellen", linkType = LinkType.FORM)
  def create: User = {
    val user = new User
    val userInfo = new UserInfo
    val address = new Address

    userInfo.image = new Media
    address.point = new GeoPoint

    user.info = userInfo
    user.address = address

    forLinks(classOf[User], (user, link) => {
      linkTo(methodOn(classOf[UserFormResource]).save(user))
        .build(link.addLink)
    })

    user
  }

  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[UserFormSchema])
  @RolesAllowed(Array("Guest", "User", "Administrator"))
  @LinkDescription(value = "Profil", linkType = LinkType.FORM)
  def read(@PathParam("id") id: UUID): User = {

    val entity: User = if (Credential.current().hasRole("Guest") || Credential.current().hasRole("User")) then
      User.current()
    else
      User.find(id)

    forLinks(classOf[User], (user, link) => {
      linkTo(methodOn(classOf[UserFormResource]).update(user))
        .build(link.addLink)
      linkTo(methodOn(classOf[UserFormResource]).delete(user))
        .build(link.addLink)
    })

    entity
  }

  @POST
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(classOf[UserFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Speichern", linkType = LinkType.FORM)
  def save(@JsonSchema(classOf[UserFormSchema]) entity: User): User = {
    entity.persist()

    forLinks(classOf[User], (user, link) => {
      linkTo(methodOn(classOf[UserFormResource]).update(user))
        .build(link.addLink)
      linkTo(methodOn(classOf[UserFormResource]).delete(user))
        .build(link.addLink)
    })


    entity
  }

  @PUT
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(classOf[UserFormSchema])
  @RolesAllowed(Array("Guest", "User", "Administrator"))
  @LinkDescription(value = "Aktualisieren", linkType = LinkType.FORM)
  def update(@JsonSchema(classOf[UserFormSchema]) @SecuredOwner entity: User): User = {
    entity.validate()

    forLinks(classOf[User], (user, link) => {
      linkTo(methodOn(classOf[UserFormResource]).update(user))
        .build(link.addLink)
      linkTo(methodOn(classOf[UserFormResource]).delete(user))
        .build(link.addLink)
    })


    entity
  }

  @DELETE
  @Produces(Array("application/json"))
  @JsonSchema(classOf[UserFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Löschen", linkType = LinkType.FORM)
  def delete(@JsonSchema(classOf[UserFormSchema]) @SecuredOwner entity: User): User = {

    forLinks(classOf[User], (user, link) => {
      linkTo(methodOn(classOf[UserTableResource]).list(null))
        .withRedirect
        .build(link.addLink)
    })

    entity.deleted = true

    entity
  }
}
