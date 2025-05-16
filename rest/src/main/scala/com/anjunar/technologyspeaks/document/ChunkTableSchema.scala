package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.RoleTableSearch
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.{ChunkSchema, GroupSchema}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class ChunkTableSchema extends EntityJSONSchema[QueryTable[ChunkTableSearch, Chunk]] {
  override def build(root: QueryTable[ChunkTableSearch, Chunk], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[QueryTable[ChunkTableSearch, Chunk]]) => builder
      .property("search", property => property
        .forType(classOf[ChunkTableSearch], (builder: EntitySchemaBuilder[ChunkTableSearch]) => builder
          .property("sort")
          .property("index")
          .property("limit")
          .property("document")
        )
      )
      .property("rows", property => property
        .withTitle("Chunks")
        .forType(classOf[Chunk], builder => ChunkSchema.static(builder))
        .forInstance(root.rows, classOf[Chunk], (entity : Chunk) => (builder  : EntitySchemaBuilder[Chunk]) => ChunkSchema.dynamic(builder, entity))
      )
      .property("size")
    )
  }

}