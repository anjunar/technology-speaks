package com.anjunar.technologyspeaks.security

import com.anjunar.technologyspeaks.control.{User, UserFormResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.scala.mapper.annotations.{Action, JsonSchema}
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class ConfirmationReturnSchema extends EntityJSONSchema[User] {
  def build(root: User, javaType: Type, action: JsonSchema.State): SchemaBuilder = {
    val builder = new SchemaBuilder()
      .forType(classOf[User], (entity: EntitySchemaBuilder[User]) => entity
        .property("id", property => property
          .withTitle("Id")
          .withWidget("text")
        )
      )
    
    builder.forType(classOf[User], (entity : EntitySchemaBuilder[User]) => entity
      .withLinks((instance, link) => {
        linkTo(methodOn(classOf[UserFormResource]).read(instance.id))
          .build(link.addLink)
      })
    )
    
    builder
  }
}

