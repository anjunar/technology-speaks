package com.anjunar.technologyspeaks.shared

import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, LinkContext, SchemaBuilder}

object TableSchema {

  def static[C](schema: SchemaBuilder): SchemaBuilder = schema
    .forType(classOf[Table[C]], (entity: EntitySchemaBuilder[Table[C]]) => {
      entity
        .property("rows")
        .property("size")
    })
}
