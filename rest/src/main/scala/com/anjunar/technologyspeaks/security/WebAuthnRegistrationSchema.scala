package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

import java.lang.reflect.Type

class WebAuthnRegistrationSchema extends EntityJSONSchema[WebAuthnLogin] {
  override def build(instance: WebAuthnLogin, aType: Type, action: JsonSchema.State): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[WebAuthnLogin], (entity: EntitySchemaBuilder[WebAuthnLogin]) => entity
      .property("username", property => property
        .withWriteable(true)
      )
      .property("displayName", property => property
        .withWriteable(true)
      )
      .withLinks((instance, link) => {
        linkTo(methodOn(classOf[WebAuthnRegistrationResource]).generateRegistrationOptions(null))
          .withRel("register")
          .build(link.addLink)
      })
    )

    builder
  }
}
