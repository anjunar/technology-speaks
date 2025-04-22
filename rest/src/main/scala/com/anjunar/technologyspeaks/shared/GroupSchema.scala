package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{Group, User, UserTableResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

object GroupSchema {

  def staticCompact(builder: EntitySchemaBuilder[Group], isOwnedOrAdmin: Boolean): EntitySchemaBuilder[Group] = {
    builder
      .property("id")
      .property("name", property => property
        .withWriteable(isOwnedOrAdmin)
      )
      .property("description", property => property
        .withWriteable(isOwnedOrAdmin)
      )
  }

  def staticFull(builder: EntitySchemaBuilder[Group], isOwnedOrAdmin: Boolean): EntitySchemaBuilder[Group] = {
    builder
      .property("id")
      .property("name", property => property
        .withWriteable(isOwnedOrAdmin)
      )
      .property("description", property => property
        .withWriteable(isOwnedOrAdmin)
      )
      .property("users", property => property
        .withWriteable(isOwnedOrAdmin)
        .withLinks((links) => {
          linkTo(methodOn(classOf[UserTableResource]).list(null))
            .build(links.addLink)
        })
        .forType(classOf[User], UserSchema.staticCompact)
      )
  }

}
