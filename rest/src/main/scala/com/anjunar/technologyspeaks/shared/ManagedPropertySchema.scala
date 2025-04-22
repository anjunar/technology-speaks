package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{Group, GroupTableResource, User, UserTableResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

object ManagedPropertySchema {

  def static(builder: EntitySchemaBuilder[ManagedProperty], isOwnedOrAdmin: Boolean) : EntitySchemaBuilder[ManagedProperty] = {

    builder
      .property("id")
      .property("visibleForAll", property => property
        .withWriteable(isOwnedOrAdmin)
      )
      .property("users", property => property
        .withWriteable(isOwnedOrAdmin)
        .forType(classOf[User], UserSchema.staticCompact)
      )
      .property("groups", property => property
        .withWriteable(isOwnedOrAdmin)
        .withLinks(links => {
          linkTo(methodOn(classOf[GroupTableResource]).list(null))
            .build(links.addLink)
        })
        .forType(classOf[Group], GroupSchema.staticCompact(_, isOwnedOrAdmin))
      )
  }

}
