package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.control.{GeoPoint, Role, User}
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jpa.Pair
import com.anjunar.technologyspeaks.media.Media
import com.anjunar.technologyspeaks.openstreetmap.geocoding.GeoService
import com.anjunar.technologyspeaks.security.{Authenticator, EmailCredential, Secured}
import com.anjunar.scala.mapper.annotations.JsonSchema.State
import com.anjunar.scala.mapper.annotations.{Action, DoNotLoad, JsonSchema, SecuredOwner}
import com.anjunar.scala.schema.model.LinkType
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
class UserFormResource {
  
  @Inject 
  var authenticator: Authenticator = uninitialized
  
  @Inject
  var entityManager : EntityManager = uninitialized
  
  @GET
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[UserFormSchema], state = State.ENTRYPOINT)
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
    user
  }

  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[UserFormSchema], state = State.READ)
  @RolesAllowed(Array("Guest", "User", "Administrator"))
  @LinkDescription(value = "Profil", linkType = LinkType.FORM)
  def read(@PathParam("id") id: UUID): User = {
    val entity: User = if (Credential.current().hasRole("Guest") || Credential.current().hasRole("User")) then 
      User.current()
    else
       User.find(id)
    entity
  }

  @POST
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[UserFormSchema], state = State.CREATE)
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Speichern", linkType = LinkType.FORM)
  def save(@JsonSchema(value = classOf[UserFormSchema], state = State.CREATE) entity: User): User = {
    entity.validate()
    entity.persist()
    entity
  }

  @PUT
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[UserFormSchema], state = State.UPDATE)
  @RolesAllowed(Array("Guest", "User", "Administrator"))
  @LinkDescription(value = "Aktualisieren", linkType = LinkType.FORM)
  def update(@JsonSchema(value = classOf[UserFormSchema], state = State.UPDATE) @SecuredOwner entity: User): User = {
    entity.validate()
    entity
  }

  @DELETE
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[UserFormSchema], state = State.DELETE)
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Löschen", linkType = LinkType.FORM)
  def delete(@JsonSchema(value = classOf[UserFormSchema], state = State.DELETE) @SecuredOwner entity: User): User = {

    val count = entityManager.createQuery("select count(e) from Event e where e.owner = :owner", classOf[Long])
      .setParameter("owner", entity)
      .getSingleResult
    
    if (count > 0) {
      entity.deleted = true
    } else {
      entity.delete()  
    }
    
    entity
  }
}
