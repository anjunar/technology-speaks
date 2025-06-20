package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.Table

import java.lang.reflect.Type
import java.util

class CodeMirrorFilesSchema extends EntityJSONSchema[Table[AbstractCodeMirrorFile]] {
  def build(root: Table[AbstractCodeMirrorFile], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(javaType, (builder: EntitySchemaBuilder[Table[AbstractCodeMirrorFile]]) => builder
      .property("rows", property => property
        .withTitle("Credentials")
        .forType(classOf[CodeMirrorTS], (builder: EntitySchemaBuilder[CodeMirrorTS]) => builder
          .property("id")
          .property("name")
          .property("content")
          .property("transpiled")
          .property("sourceMap")
        )
        .forType(classOf[CodeMirrorImage], (builder: EntitySchemaBuilder[CodeMirrorImage]) => builder
          .property("id")
          .property("name")
          .property("data")
          .property("contentType")
        )
        .forType(classOf[CodeMirrorCSS], (builder: EntitySchemaBuilder[CodeMirrorCSS]) => builder
          .property("id")
          .property("name")
          .property("content")
        )
        .forType(classOf[CodeMirrorHTML], (builder: EntitySchemaBuilder[CodeMirrorHTML]) => builder
          .property("id")
          .property("name")
          .property("content")
        )
      )
      .property("size")
    )

  }
}
