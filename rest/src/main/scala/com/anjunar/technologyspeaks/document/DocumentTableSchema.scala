package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.DocumentSchema

import java.lang.reflect.Type

class DocumentTableSchema extends EntityJSONSchema[Table[Document]] {
  override def build(root: Table[Document], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[QueryTable[DocumentSearch, Document]]) => builder
      .property("search", property => property
        .forType(classOf[DocumentSearch], (builder: EntitySchemaBuilder[DocumentSearch]) => builder
          .property("text", property => property
            .withTitle("Text")
          )
        )
      )
      .property("rows", property => property
        .withTitle("Documents")
        .forInstance(root.rows, classOf[Document], (entity : Document) => builder => DocumentSchema.static(builder, entity))
      )
      .property("size")
    )
  }

}