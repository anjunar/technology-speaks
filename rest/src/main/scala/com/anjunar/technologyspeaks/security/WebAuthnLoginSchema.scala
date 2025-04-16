package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

import java.lang.reflect.Type

class WebAuthnLoginSchema extends EntityJSONSchema[WebAuthnLogin] {
  override def build(instance: WebAuthnLogin, aType: Type): SchemaBuilder = {
    new SchemaBuilder()
      .forType(classOf[WebAuthnLogin], (entity: EntitySchemaBuilder[WebAuthnLogin]) => entity
        .property("username", property => property
          .withWriteable(true)
        )
      )
  }
}
