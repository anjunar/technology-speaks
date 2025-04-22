package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{Group, GroupTableResource, User, UserTableResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

object ManagedPropertySchema {

  def static(builder: EntitySchemaBuilder[ManagedProperty], isOwnedOrAdmin: Boolean) : EntitySchemaBuilder[ManagedProperty] = {

    builder
      .property("visibleForAll")
      .property("users", property => property
        .withWriteable(isOwnedOrAdmin)
        .forType(classOf[User], UserSchema.staticCompact)
      )
      .property("groups", property => property
        .withWriteable(isOwnedOrAdmin)
        .forType(classOf[Group], GroupSchema.staticCompact(_, isOwnedOrAdmin))
      )
  }

}
