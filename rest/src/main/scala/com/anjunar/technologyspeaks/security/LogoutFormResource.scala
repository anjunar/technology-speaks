package com.anjunar.technologyspeaks.security

import com.anjunar.technologyspeaks.ApplicationFormResource
import com.anjunar.technologyspeaks.control.{Credential, EMail, User}
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.scala.mapper.annotations.JsonSchema.State
import com.anjunar.scala.mapper.annotations.{Action, JsonSchema}
import com.anjunar.scala.schema.model.LinkType
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.{Context, Response, SecurityContext}

import java.util.UUID
import scala.compiletime.uninitialized


@Path("security/logout")
@ApplicationScoped
@Secured class LogoutFormResource {
  
  @Inject var authenticator: Authenticator = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[LogoutFormSchema], state = State.ENTRYPOINT)
  @RolesAllowed(Array("Guest", "User", "Administrator"))
  @LinkDescription(value = "Abmelden", linkType = LinkType.FORM)
  def logout(): Credential = Credential.current()

  @POST
  @Produces(Array("application/json"))
  @Consumes(Array("application/json"))
  @RolesAllowed(Array("Guest", "User", "Administrator"))
  @LinkDescription(value = "Abmelden", linkType = LinkType.FORM)
  def logout(@JsonSchema(value = classOf[LogoutFormSchema], state = State.EXECUTE) entity: Credential): Response = {
    authenticator.logout()
    Response.ok().build()
  }
}
