package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{Address, GeoPoint}

object AddressSchema {

  def static(builder: EntitySchemaBuilder[Address], isOwnedOrAdmin: Boolean): EntitySchemaBuilder[Address] = {
    builder
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
      .property("point", property => property
        .forType(classOf[GeoPoint], GeoPointSchema.static)
      )

  }

}
