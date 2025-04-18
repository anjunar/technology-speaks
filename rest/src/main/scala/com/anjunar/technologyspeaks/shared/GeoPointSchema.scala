package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.GeoPoint

object GeoPointSchema {

  def static(builder: SchemaBuilder, isOwnedOrAdmin: Boolean): Unit = {
    builder.forType(classOf[GeoPoint], (entity: EntitySchemaBuilder[GeoPoint]) => entity
      .property("x")
      .property("y")
    )
  }


}
