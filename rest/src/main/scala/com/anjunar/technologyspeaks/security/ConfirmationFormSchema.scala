package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class ConfirmationFormSchema extends EntityJSONSchema[Confirmation] {
  def build(root: Confirmation, javaType: Type): SchemaBuilder = {
    new SchemaBuilder()
      .forType(classOf[Confirmation], (entity: EntitySchemaBuilder[Confirmation]) => entity
        .property("code", property => property
          .withTitle("Code")
          .withWidget("text")
          .withWriteable(true)
        )
      )
  }
}
