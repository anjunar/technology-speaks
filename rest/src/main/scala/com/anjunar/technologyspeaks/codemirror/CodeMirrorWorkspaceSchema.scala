package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, FullEntitySchema, SchemaBuilder}

import java.lang.reflect.Type

class CodeMirrorWorkspaceSchema extends EntityJSONSchema[CodeMirrorWorkspace] {
  def build(root: CodeMirrorWorkspace, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[CodeMirrorWorkspace], (builder: EntitySchemaBuilder[CodeMirrorWorkspace]) => builder
      .property("id")
      .property("open", property => property
        .forType(classOf[CodeMirrorTS], FullEntitySchema.analyse(_))
        .forType(classOf[CodeMirrorImage], FullEntitySchema.analyse(_))
        .forType(classOf[CodeMirrorCSS], FullEntitySchema.analyse(_))
        .forType(classOf[CodeMirrorHTML], FullEntitySchema.analyse(_))
      )
    )


  }
}
