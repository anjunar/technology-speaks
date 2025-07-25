package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, FullEntitySchema, SchemaBuilder}

import java.lang.reflect.Type

class CodeMirrorFileSchema extends EntityJSONSchema[AbstractCodeMirrorFile] {
  def build(root: AbstractCodeMirrorFile, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder
      .forType(classOf[CodeMirrorTS], FullEntitySchema.analyse(_))
      .forType(classOf[CodeMirrorCSS], FullEntitySchema.analyse(_))
      .forType(classOf[CodeMirrorHTML], FullEntitySchema.analyse(_))
      .forType(classOf[CodeMirrorImage], FullEntitySchema.analyse(_))
    
  }
}
