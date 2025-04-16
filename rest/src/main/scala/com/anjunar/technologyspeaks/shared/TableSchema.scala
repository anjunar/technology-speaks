package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, LinkContext, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.Table

object TableSchema {

  def static[C](schema: SchemaBuilder): SchemaBuilder = schema
    .forType(classOf[Table[C]], (entity: EntitySchemaBuilder[Table[C]]) => {
      entity
        .property("rows")
        .property("size")
    })
}
