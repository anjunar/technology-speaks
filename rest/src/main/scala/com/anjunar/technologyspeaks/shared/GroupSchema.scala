package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{Credential, Group, User, UserTableResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

import java.util

object GroupSchema {

  def compact(builder: EntitySchemaBuilder[Group], loaded : Group): EntitySchemaBuilder[Group] = {
    val credential = Credential.current()
    val currentUser = User.current()
    val isOwnedOrAdmin = loaded.owner == currentUser || credential.hasRole("Administrator")

    builder
      .property("id")
      .property("name", property => property
        .withWriteable(isOwnedOrAdmin)
      )
      .property("description", property => property
        .withWriteable(isOwnedOrAdmin)
      )
  }

  def full(builder: EntitySchemaBuilder[Group], loaded : Group): EntitySchemaBuilder[Group] = {

    val credential = Credential.current()
    val currentUser = User.current()
    val isOwnedOrAdmin = loaded.owner == currentUser || credential.hasRole("Administrator")

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
        .forInstance(loaded.users, classOf[User], (entity : User) => builder => UserSchema.dynamicCompact(builder, entity))
      )
  }


}
