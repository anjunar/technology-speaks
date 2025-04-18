package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.Address

object AddressSchema {

  def static(builder: SchemaBuilder, isOwnedOrAdmin: Boolean): Unit = {
    builder.forType(classOf[Address], (entity: EntitySchemaBuilder[Address]) => entity
      .property("id")
      .property("street", property => property
        .withWriteable(isOwnedOrAdmin)
      )
      .property("number", property => property
        .withWriteable(isOwnedOrAdmin)
      )
      .property("zipCode", property => property
        .withWriteable(isOwnedOrAdmin)
      )
      .property("country", property => property
        .withWriteable(isOwnedOrAdmin)
      )
      .property("point")
    )

  }


}
