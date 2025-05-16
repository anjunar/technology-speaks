package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.DocumentSchema

import java.lang.reflect.Type

class DocumentTableSchema extends EntityJSONSchema[QueryTable[DocumentTableSearch, Document]] {
  override def build(root: QueryTable[DocumentTableSearch, Document], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[QueryTable[DocumentTableSearch, Document]]) => builder
      .property("search", property => property
        .forType(classOf[DocumentTableSearch], (builder: EntitySchemaBuilder[DocumentTableSearch]) => builder
          .property("sort")
          .property("index")
          .property("limit")
          .property("text")
        )
      )
      .property("rows", property => property
        .withTitle("Documents")
        .forType(classOf[Document], builder => DocumentSchema.static(builder))
        .forInstance(root.rows, classOf[Document], (entity : Document) => builder => DocumentSchema.dynamic(builder, entity))
      )
      .property("size")
    )
  }

}