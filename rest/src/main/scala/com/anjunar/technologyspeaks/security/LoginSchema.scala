package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

import java.lang.reflect.Type

class LoginSchema extends EntityJSONSchema[Login] {
  override def build(instance: Login, aType: Type): SchemaBuilder = {
    new SchemaBuilder()
      .forType(classOf[Login], (entity: EntitySchemaBuilder[Login]) => entity
        .property("username", property => property
          .withWriteable(true)
        )
        .property("password", property => property
          .withWriteable(true)
        )
      )
  }
}
