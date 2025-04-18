package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.Group

object GroupSchema {

  def static(builder: SchemaBuilder, isOwnedOrAdmin: Boolean) : SchemaBuilder = {

    builder.forType(classOf[Group], (builder : EntitySchemaBuilder[Group]) => builder
      .property("id")
      .property("name", property => property
        .withWriteable(isOwnedOrAdmin)
      )
    )

  }

}
