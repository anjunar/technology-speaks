package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.EMail

object EMailSchema {

  def static(builder: SchemaBuilder): Unit = {
    builder.forType(classOf[EMail], (entity: EntitySchemaBuilder[EMail]) => entity
      .property("value")
    )
  }

  def static(builder: EntitySchemaBuilder[EMail]): EntitySchemaBuilder[EMail] = {
    builder.property("value")
  }


}
