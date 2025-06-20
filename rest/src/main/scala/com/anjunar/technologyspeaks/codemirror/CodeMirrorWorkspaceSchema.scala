package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.Table

import java.lang.reflect.Type

class CodeMirrorWorkspaceSchema extends EntityJSONSchema[CodeMirrorWorkspace] {
  def build(root: CodeMirrorWorkspace, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[CodeMirrorWorkspace], (builder: EntitySchemaBuilder[CodeMirrorWorkspace]) => builder
      .property("id")
      .property("open", property => property
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
    )


  }
}
