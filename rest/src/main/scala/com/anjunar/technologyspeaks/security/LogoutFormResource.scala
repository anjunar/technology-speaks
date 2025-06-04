package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.ApplicationFormResource
import com.anjunar.technologyspeaks.control.{Credential, EMail, User}
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.{Context, Response, SecurityContext}

import java.util.UUID
import scala.compiletime.uninitialized


@Path("security/logout")
@ApplicationScoped
@Secured class LogoutFormResource extends SchemaBuilderContext {

  @Inject var authenticator: Authenticator = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[LogoutFormSchema])
  @RolesAllowed(Array("Guest", "User", "Administrator"))
  @LinkDescription(value = "Logout", linkType = LinkType.FORM)
  def logout(): Credential = {

    forLinks(classOf[Credential], (instance, link) => {
      linkTo(methodOn(classOf[LogoutFormResource]).logout(null))
        .build(link.addLink)
    })


    Credential.current()
  }

  @POST
  @Produces(Array("application/json"))
  @Consumes(Array("application/json"))
  @RolesAllowed(Array("Guest", "User", "Administrator"))
  @LinkDescription(value = "Logout", linkType = LinkType.FORM)
  def logout(@JsonSchema(classOf[LogoutFormSchema]) entity: Credential): Response = {
    authenticator.logout()

    forLinks(classOf[Credential], (instance, link) => {
      linkTo(methodOn(classOf[ApplicationFormResource]).service())
        .withRedirect
        .build(link.addLink)
    })

    Response.ok().build()
  }
}
