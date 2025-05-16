package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.{JsonSchema, SecuredOwner}
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.Credential
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.security.Secured
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.*

import java.util.UUID

@Path("security/credentials/credential")
@ApplicationScoped
@Secured
class CredentialFormResource extends SchemaBuilderContext {


  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[CredentialFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Read", linkType = LinkType.FORM)
  @SecuredOwner
  def read(@PathParam("id") id: UUID): Credential = {

    forLinks(classOf[Credential], (instance, links) => {
      linkTo(methodOn(classOf[CredentialFormResource]).update(instance))
        .build(links.addLink)
    })

    Credential.find(id)
  }

  @PUT
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(classOf[CredentialFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Update", linkType = LinkType.FORM)
  def update(@JsonSchema(classOf[CredentialFormSchema]) @SecuredOwner entity: Credential): Credential = {
    entity.validate()
    entity
  }


}
