package com.anjunar.technologyspeaks.timeline

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{RoleSearch, User, UserTableResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.{PostSchema, UserSchema}

import java.lang.reflect.Type

class PostTableSchema extends EntityJSONSchema[QueryTable[PostSearch, Post]] {
  override def build(root: QueryTable[PostSearch, Post], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[QueryTable[PostSearch, Post]]) => builder
      .property("rows", property => property
        .withTitle("Posts")
        .forType(classOf[Post], builder => PostSchema.static(builder))
        .forInstance(root.rows, classOf[Post], (entity : Post) => builder => PostSchema.dynamic(builder, entity))
      )
      .property("size")
    )
  }

}