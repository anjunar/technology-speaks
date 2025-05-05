package com.anjunar.technologyspeaks.timeline

import com.anjunar.scala.schema.builder.{EntityJSONSchema, SchemaBuilder}
import com.anjunar.technologyspeaks.shared.PostSchema

import java.lang.reflect.Type

class PostFormSchema extends EntityJSONSchema[Post] {
  override def build(root: Post, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[Post], PostSchema.static(_, root))
  }
}

