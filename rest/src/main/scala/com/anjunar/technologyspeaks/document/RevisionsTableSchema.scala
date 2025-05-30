package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PrimitiveSchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.DocumentSchema
import jakarta.persistence.Tuple

import java.lang.reflect.Type

class RevisionsTableSchema extends EntityJSONSchema[Table[Document]] {
  override def build(root: Table[Document], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[Table[Document]]) => builder
      .property("rows", property => property
        .withTitle("Documents")
        .forType(classOf[Document], builder => DocumentSchema.static(builder))
        .forInstance(root.rows, classOf[Document], (entity : Document) => builder => DocumentSchema.dynamic(builder, entity))
      )
      .property("size")
    )
  }

}