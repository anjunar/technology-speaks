package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.DocumentSchema

import java.lang.reflect.Type

class RevisionSearchSchema extends EntityJSONSchema[RevisionSearch] {
  override def build(root: RevisionSearch, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[RevisionSearch], (builder: EntitySchemaBuilder[RevisionSearch]) => builder
      .property("sort")
      .property("index")
      .property("limit")
      .property("document", property => property
        .forType(classOf[Document], DocumentSchema.staticCompact)
      )
    )

  }

}