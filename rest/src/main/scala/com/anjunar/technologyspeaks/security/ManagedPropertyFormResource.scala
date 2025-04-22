package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.{JsonSchema, SecuredOwner}
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.shared.ManagedProperty
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.{Consumes, GET, PUT, Path, PathParam, Produces}

import java.util.UUID

@Path("security/properties/property")
@ApplicationScoped
@Secured
class ManagedPropertyFormResource extends SchemaBuilderContext {


  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[ManagedPropertyFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Read", linkType = LinkType.FORM)
  @SecuredOwner
  def read(@PathParam("id") id: UUID): ManagedProperty = {

    forLinks(classOf[ManagedProperty], (instance, links) => {
      linkTo(methodOn(classOf[ManagedPropertyFormResource]).update(instance))
        .build(links.addLink)
    })

    ManagedProperty.find(id)
  }

  @PUT
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(classOf[ManagedPropertyFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Update", linkType = LinkType.FORM)
  def update(@JsonSchema(classOf[ManagedPropertyFormSchema]) @SecuredOwner entity: ManagedProperty): ManagedProperty = {
    entity.validate()
    entity
  }


}
