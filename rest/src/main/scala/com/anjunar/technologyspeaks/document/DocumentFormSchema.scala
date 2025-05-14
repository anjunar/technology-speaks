package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, SchemaBuilder}
import com.anjunar.technologyspeaks.shared.DocumentSchema

import java.lang.reflect.Type

class DocumentFormSchema extends EntityJSONSchema[Document] {
  override def build(root: Document, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[Document], DocumentSchema.dynamic(_, root))
  }
}

