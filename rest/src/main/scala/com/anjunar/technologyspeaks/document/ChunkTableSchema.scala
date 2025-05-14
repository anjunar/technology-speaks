package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.shared.{GroupSchema, ChunkSchema}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class ChunkTableSchema extends EntityJSONSchema[Table[Chunk]] {
  override def build(root: Table[Chunk], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[Table[Chunk]], (builder: EntitySchemaBuilder[Table[Chunk]]) => builder
      .property("rows", property => property
        .withTitle("Chunks")
        .forType(classOf[Chunk], builder => ChunkSchema.static(builder))
        .forInstance(root.rows, classOf[Chunk], (entity : Chunk) => (builder  : EntitySchemaBuilder[Chunk]) => ChunkSchema.dynamic(builder, entity))
      )
      .property("size")
    )
  }

}