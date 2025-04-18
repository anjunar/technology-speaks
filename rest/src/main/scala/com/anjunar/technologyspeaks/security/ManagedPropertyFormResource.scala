package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.shared.ManagedProperty
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.{Consumes, GET, PUT, Path, PathParam, Produces}

import java.util.UUID

@Path("security/properties/property")
@ApplicationScoped
@Secured
class ManagedPropertyFormResource {


  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[ManagedPropertyFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Read", linkType = LinkType.FORM)
  def read(@PathParam("id") id: UUID): ManagedProperty = {
    ManagedProperty.find(id)
  }

  @PUT
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(classOf[ManagedPropertyFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Update", linkType = LinkType.FORM)
  def update(@JsonSchema(classOf[ManagedPropertyFormSchema]) entity: ManagedProperty): ManagedProperty = {
    entity.validate()
    entity
  }


}
