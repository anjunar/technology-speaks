package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{Address, Credential, GeoPoint, User}

object AddressSchema {

  def static(builder: EntitySchemaBuilder[Address], loaded : Address): EntitySchemaBuilder[Address] = {

    val current = User.current()
    val isOwnedOrAdmin = current == loaded.user.owner || Credential.current().hasRole("Administrator")

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
