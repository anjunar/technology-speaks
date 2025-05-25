package com.anjunar.technologyspeaks.timeline

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{User, UserTableResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.QueryTable
import com.anjunar.technologyspeaks.shared.{PostSchema, UserSchema}

import java.lang.reflect.Type

class PostSearchSchema extends EntityJSONSchema[PostSearch] {
  override def build(root: PostSearch, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[PostSearch], (builder: EntitySchemaBuilder[PostSearch]) => builder
      .property("sort")
      .property("index")
      .property("limit")
      .property("user", property => property
        .forType(classOf[User], UserSchema.static)
        .withLinks(links => {
          linkTo(methodOn(classOf[UserTableResource]).list(null))
            .build(links.addLink)
        })
      )
    )

  }

}