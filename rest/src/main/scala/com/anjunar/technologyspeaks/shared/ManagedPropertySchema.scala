package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{GroupTableResource, User, UserTableResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

object ManagedPropertySchema {

  def static(builder: SchemaBuilder, isOwnedOrAdmin: Boolean) : SchemaBuilder = {

    builder.forType(classOf[ManagedProperty], (builder : EntitySchemaBuilder[ManagedProperty]) => builder
      .property("id")
      .property("visibleForAll")
      .property("users", property => property
        .withWriteable(isOwnedOrAdmin)
      )
      .property("groups", property => property
        .withWriteable(isOwnedOrAdmin)
      )
    )

    GroupSchema.static(builder, isOwnedOrAdmin)
    UserSchema.staticCompact(builder, isOwnedOrAdmin)
  }

}
