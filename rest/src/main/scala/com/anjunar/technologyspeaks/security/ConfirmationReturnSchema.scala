package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{User, UserFormResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class ConfirmationReturnSchema extends EntityJSONSchema[User] {
  def build(root: User, javaType: Type): SchemaBuilder = {
    new SchemaBuilder()
      .forType(classOf[User], (entity: EntitySchemaBuilder[User]) => entity
        .property("id", property => property
          .withTitle("Id")
          .withWidget("text")
        )
      )
  }
}

