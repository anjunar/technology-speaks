package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, FullEntitySchema, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.Table

import java.lang.reflect.Type

class CodeMirrorFilesSchema extends EntityJSONSchema[Table[AbstractCodeMirrorFile]] {
  def build(root: Table[AbstractCodeMirrorFile], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(javaType, (builder: EntitySchemaBuilder[Table[AbstractCodeMirrorFile]]) => builder
      .property("rows", property => property
        .withTitle("Credentials")
        .forType(classOf[CodeMirrorTS], FullEntitySchema.analyse(_))
        .forType(classOf[CodeMirrorImage], FullEntitySchema.analyse(_))
        .forType(classOf[CodeMirrorCSS], FullEntitySchema.analyse(_))
        .forType(classOf[CodeMirrorHTML], FullEntitySchema.analyse(_))
      )
      .property("size")
    )

  }
}
