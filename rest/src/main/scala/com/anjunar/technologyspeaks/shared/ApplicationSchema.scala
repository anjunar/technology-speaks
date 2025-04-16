package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, LinkContext, SchemaBuilder}
import com.anjunar.technologyspeaks.Application

object ApplicationSchema {

  def read(builder: SchemaBuilder): Unit = builder
    .forType(classOf[Application], (entity: EntitySchemaBuilder[Application]) => entity
      .property("user")
    )

}
