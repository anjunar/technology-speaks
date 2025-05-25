package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.DocumentSchema

import java.lang.reflect.Type

class DocumentSearchSchema extends EntityJSONSchema[DocumentSearch] {
  override def build(root: DocumentSearch, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[DocumentSearch], (builder: EntitySchemaBuilder[DocumentSearch]) => builder
      .property("sort")
      .property("index")
      .property("limit")
      .property("text")
    )

  }

}