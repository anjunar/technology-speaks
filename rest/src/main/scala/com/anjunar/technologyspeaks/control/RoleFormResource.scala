package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.security.Secured
import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.mapper.annotations.JsonSchema.State
import com.anjunar.scala.schema.model.LinkType
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.*

import java.util.UUID


@Path("control/roles/role")
@ApplicationScoped
@Secured
class RoleFormResource {

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[RoleFormSchema], state = State.ENTRYPOINT)
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Rolle erstellen", linkType = LinkType.FORM)
  def create: Role = new Role

  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[RoleFormSchema], state = State.READ)
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Lesen", linkType = LinkType.FORM)
  def read(@PathParam("id") id: UUID): Role = Role.find(id)

  @POST
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[RoleFormSchema], state = State.CREATE)
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Speichern", linkType = LinkType.FORM)
  def save(@JsonSchema(value = classOf[RoleFormSchema], state = State.CREATE) entity: Role): Role = {
    entity.validate()
    entity.persist()
    entity
  }

  @PUT
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[RoleFormSchema], state = State.UPDATE)
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Aktualisieren", linkType = LinkType.FORM)
  def update(@JsonSchema(value = classOf[RoleFormSchema], state = State.UPDATE) entity: Role): Role = {
    entity.validate()
    entity
  }

  @DELETE
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[RoleFormSchema], state = State.DELETE)
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Löschen", linkType = LinkType.FORM)
  def delete(@JsonSchema(value = classOf[RoleFormSchema], state = State.DELETE) entity: Role): Role = {
    entity.delete()
    entity
  }
}
