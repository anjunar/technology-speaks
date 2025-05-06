package com.anjunar.technologyspeaks

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.*
import com.anjunar.technologyspeaks.document.{DocumentTableResource, DocumentTableSchema}
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.security.*
import com.anjunar.technologyspeaks.timeline.PostTableResource
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.{Context, SecurityContext}
import jakarta.ws.rs.{GET, Path, Produces}

import java.security.Principal
import java.util.{Objects, UUID}


@Path("/")
@ApplicationScoped 
class ApplicationFormResource extends SchemaBuilderContext {
  
  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[ApplicationFormSchema])
  def service(): Application = {

    val principal = Credential.current()
    if (Objects.isNull(principal)) {
      val user = new User

      val email = new EMail()
      email.value = "gast@web.com"

      user.emails.add(email)

      forLinks(classOf[Application], (instance, link) => {
        linkTo(methodOn(classOf[WebAuthnLoginResource]).entry())
          .withRel("login")
          .build(link.addLink)

        linkTo(methodOn(classOf[WebAuthnRegistrationResource]).entry())
          .withRel("register")
          .build(link.addLink)
      })


      new Application(user)
    }
    else {
      val user = principal.email.user

      forLinks(classOf[Application], (instance, link) => {
        if (principal.validated) {
          linkTo(methodOn(classOf[UserFormResource]).read(user.id))
            .withRel("profile")
            .build(link.addLink)

          linkTo(methodOn(classOf[UserTableResource]).list(null))
            .withRel("users")
            .build(link.addLink)

          linkTo(methodOn(classOf[RoleTableResource]).list(null))
            .withRel("roles")
            .build(link.addLink)

          linkTo(methodOn(classOf[GroupTableResource]).list(null))
            .withRel("groups")
            .build(link.addLink)

          linkTo(methodOn(classOf[CredentialTableResource]).list(null))
            .withRel("devices")
            .build(link.addLink)

          linkTo(methodOn(classOf[PostTableResource]).list(null))
            .withRel("timeline")
            .build(link.addLink)

          linkTo(methodOn(classOf[DocumentTableResource]).list(null))
            .withRel("documents")
            .build(link.addLink)

        } else {
          linkTo(methodOn(classOf[ConfirmationFormResource]).create)
            .withRel("confirm")
            .build(link.addLink)
        }

        linkTo(methodOn(classOf[LogoutFormResource]).logout())
          .build(link.addLink)
      })


      new Application(user)
    }
  }
}
