package com.anjunar.technologyspeaks.timeline

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.shared.PostSchema

import java.lang.reflect.Type

class PostTableSchema extends EntityJSONSchema[Table[Post]] {
  override def build(root: Table[Post], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[Table[Post]], (builder: EntitySchemaBuilder[Table[Post]]) => builder
      .property("rows", property => property
        .withTitle("Posts")
        .forType(classOf[Post], builder => PostSchema.static(builder))
        .forInstance(root.rows, classOf[Post], (entity : Post) => builder => PostSchema.dynamic(builder, entity))
      )
      .property("size")
    )
  }

}