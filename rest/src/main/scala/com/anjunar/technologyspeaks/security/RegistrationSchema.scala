package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

import java.lang.reflect.Type

class RegistrationSchema extends EntityJSONSchema[Login] {
  override def build(instance: Login, aType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[Login], (entity: EntitySchemaBuilder[Login]) => entity
      .property("username", property => property
        .withWriteable(true)
      )
      .property("displayName", property => property
        .withWriteable(true)
      )
    )

    builder
  }
}
