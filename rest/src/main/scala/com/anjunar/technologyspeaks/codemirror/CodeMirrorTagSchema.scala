package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, FullEntitySchema, SchemaBuilder}

import java.lang.reflect.Type

class CodeMirrorTagSchema extends EntityJSONSchema[CodeMirrorTag] {
  def build(root: CodeMirrorTag, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[CodeMirrorTag], (builder : EntitySchemaBuilder[CodeMirrorTag]) =>
      FullEntitySchema.analyse(
        builder
          .property("files", property => property
            .forType(classOf[CodeMirrorTS], FullEntitySchema.analyse(_))
            .forType(classOf[CodeMirrorCSS], FullEntitySchema.analyse(_))
            .forType(classOf[CodeMirrorHTML], FullEntitySchema.analyse(_))
            .forType(classOf[CodeMirrorImage], FullEntitySchema.analyse(_))
          )
      )
    )
  }
}

