package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.{ChunkSchema, DocumentSchema}

import java.lang.reflect.Type

class ChunkSearchSchema extends EntityJSONSchema[ChunkSearch] {
  override def build(root: ChunkSearch, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[ChunkSearch], (builder: EntitySchemaBuilder[ChunkSearch]) => builder
      .property("sort")
      .property("index")
      .property("limit")
      .property("document", property => property
        .forType(classOf[Document], DocumentSchema.staticCompact)
      )
    )

  }

}