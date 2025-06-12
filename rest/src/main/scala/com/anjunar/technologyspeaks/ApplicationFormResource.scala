package com.anjunar.technologyspeaks

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.technologyspeaks.control.*
import com.anjunar.technologyspeaks.document.{DocumentSearch, DocumentTableResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.security.*
import com.anjunar.technologyspeaks.shared.i18n.{I18nSearch, I18nTableResource}
import com.anjunar.technologyspeaks.timeline.{PostSearch, PostTableResource}
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.{GET, Path, Produces}

import java.util.Objects


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
        linkTo(methodOn(classOf[LoginResource]).entry())
          .withRel("login")
          .build(link.addLink)

        linkTo(methodOn(classOf[RegistrationResource]).entry())
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

          linkTo(methodOn(classOf[UserTableResource]).search(new UserSearch))
            .withRel("users")
            .build(link.addLink)

          linkTo(methodOn(classOf[RoleTableResource]).search(new RoleSearch))
            .withRel("roles")
            .build(link.addLink)

          linkTo(methodOn(classOf[GroupTableResource]).search(new GroupSearch))
            .withRel("groups")
            .build(link.addLink)

          linkTo(methodOn(classOf[CredentialTableResource]).search(new CredentialSearch))
            .withRel("devices")
            .build(link.addLink)

          linkTo(methodOn(classOf[PostTableResource]).search(new PostSearch))
            .withRel("timeline")
            .build(link.addLink)

          linkTo(methodOn(classOf[DocumentTableResource]).search(new DocumentSearch))
            .withRel("documents")
            .build(link.addLink)

          linkTo(methodOn(classOf[I18nTableResource]).search(new I18nSearch))
            .withRel("translations")
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
