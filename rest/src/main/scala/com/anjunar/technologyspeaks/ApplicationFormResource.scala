package com.anjunar.technologyspeaks

import com.anjunar.technologyspeaks.control.{EMail, RoleTableResource, Credential, User, UserFormResource, UserTableResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.security.LogoutFormResource
import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.mapper.annotations.JsonSchema.State
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.{GET, Path, Produces}
import jakarta.ws.rs.core.{Context, SecurityContext}

import java.security.Principal
import java.util.{Objects, UUID}


@Path("/")
@ApplicationScoped 
class ApplicationFormResource {
  
  @GET
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[ApplicationFormSchema], state = State.READ)
  def service(): Application = {

    val principal = Credential.current()
    if (Objects.isNull(principal)) {
      val user = new User

      val email = new EMail()
      email.value = "gast@host.de"

      println("email: " + email.value)

      user.emails.add(email)
      new Application(user)
    }
    else {
      new Application(principal.email.user)
    }
  }
}
