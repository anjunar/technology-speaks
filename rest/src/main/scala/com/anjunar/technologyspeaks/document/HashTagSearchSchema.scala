package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}

import java.lang.reflect.Type

class HashTagSearchSchema extends EntityJSONSchema[HashTagSearch] {
  override def build(root: HashTagSearch, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[HashTagSearch], (builder: EntitySchemaBuilder[HashTagSearch]) => builder
      .property("sort")
      .property("index")
      .property("limit")
      .property("value")
    )

  }

}