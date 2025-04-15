package com.anjunar.technologyspeaks.security

import com.anjunar.technologyspeaks.ApplicationFormResource
import com.anjunar.technologyspeaks.control.{Credential, User}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.shared.UserSchema
import com.anjunar.scala.mapper.annotations.{Action, JsonSchema}
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}

import java.lang.annotation.Annotation
import java.lang.reflect.Type
import com.anjunar.scala.mapper.annotations.JsonSchema.State


class LogoutFormSchema extends EntityJSONSchema[Credential] {
  def build(root: Credential, javaType: Type, action: State): SchemaBuilder = {
    val builder = new SchemaBuilder()
    
    builder.forType(classOf[Credential], (entity : EntitySchemaBuilder[Credential]) => entity
      .property("displayName")
      .withLinks((instance, link) => {
        action match
          case State.ENTRYPOINT =>
            linkTo(methodOn(classOf[LogoutFormResource]).logout(null))
              .build(link.addLink)
          case State.EXECUTE =>
            linkTo(methodOn(classOf[ApplicationFormResource]).service())
              .withRedirect
              .build(link.addLink)
      })
    )
    
    builder
  }
}

