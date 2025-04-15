package com.anjunar.technologyspeaks.security

import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.scala.mapper.annotations.{Action, JsonSchema}
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class ConfirmationFormSchema extends EntityJSONSchema[Confirmation] {
  def build(root: Confirmation, javaType: Type, action: JsonSchema.State): SchemaBuilder = {

    val user = User.current()

    val builder = new SchemaBuilder()
      .forType(classOf[Confirmation], (entity: EntitySchemaBuilder[Confirmation]) => entity
        .property("code", property => property
          .withTitle("Code")
          .withWidget("text")
          .withWriteable(true)
        )
      )
    
    builder.forType(classOf[Confirmation], (entity : EntitySchemaBuilder[Confirmation]) => entity
      .withLinks((instance, link) => {
        linkTo(methodOn(classOf[ConfirmationFormResource]).confirm(null))
          .build(link.addLink)
        
        user
          .emails
          .stream()
          .flatMap(email => email.credentials.stream())
          .filter(token => ! token.validated)
          .forEach(token => {
            linkTo(methodOn(classOf[ConfirmationFormResource]).reSend(token.email.value))
              .withRel("resend:" + token.email.value)
              .build(link.addLink)
          })

      })
    )
    
    builder
  }
}
