package com.anjunar.technologyspeaks

import com.anjunar.technologyspeaks.control.*
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.security.{ConfirmationFormResource, CredentialTableResource, LogoutFormResource, WebAuthnLoginResource, WebAuthnRegistrationResource}
import com.anjunar.technologyspeaks.shared.{ApplicationSchema, UserSchema}
import com.anjunar.scala.mapper.annotations.{Action, JsonSchema}
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import jakarta.ws.rs.core.SecurityContext

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class ApplicationFormSchema extends EntityJSONSchema[Application] {
  override def build(root: Application, javaType: Type, action: JsonSchema.State): SchemaBuilder =
    val token = Credential.current()
    val builder = new SchemaBuilder()
    
    ApplicationSchema.read(builder)
    UserSchema.staticForService(builder)
    
    builder.forType(classOf[Application], (entity : EntitySchemaBuilder[Application]) => entity
      .withLinks((instance, link) => {
        if (token == null) {
          linkTo(methodOn(classOf[WebAuthnLoginResource]).entry())
            .withRel("login")
            .build(link.addLink)

          linkTo(methodOn(classOf[WebAuthnRegistrationResource]).entry())
            .withRel("register")
            .build(link.addLink)
        } else {
          if (token.validated) {
            linkTo(methodOn(classOf[UserFormResource]).read(root.user.id))
              .withRel("profile")
              .build(link.addLink)

            linkTo(methodOn(classOf[UserTableResource]).list(null))
              .withRel("users")
              .build(link.addLink)

            linkTo(methodOn(classOf[RoleTableResource]).list(null))
              .withRel("roles")
              .build(link.addLink)

            linkTo(methodOn(classOf[CredentialTableResource]).list(null))
              .withRel("devices")
              .build(link.addLink)
          } else {
            linkTo(methodOn(classOf[ConfirmationFormResource]).create)
              .withRel("confirm")
              .build(link.addLink)
          }

          linkTo(methodOn(classOf[LogoutFormResource]).logout())
            .build(link.addLink)
        }
      })
    )
    
    builder

}

