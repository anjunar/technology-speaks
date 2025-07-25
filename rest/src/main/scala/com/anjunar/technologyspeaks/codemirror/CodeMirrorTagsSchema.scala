package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, FullEntitySchema, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.Table

import java.lang.reflect.Type

class CodeMirrorTagsSchema extends EntityJSONSchema[Table[CodeMirrorTag]] {
  def build(root: Table[CodeMirrorTag], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(javaType, (builder : EntitySchemaBuilder[Table[CodeMirrorTag]]) => builder
      .property("rows", property => property
        .forType(classOf[CodeMirrorTag], FullEntitySchema.analyse(_, "files"))
      )
      .property("size")
    )

  }
}

