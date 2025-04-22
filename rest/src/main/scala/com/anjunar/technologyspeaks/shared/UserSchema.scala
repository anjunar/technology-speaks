package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, LinkContext, PropertyBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.*
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{createProxy, linkTo, methodOn}
import com.anjunar.technologyspeaks.media.{Media, Thumbnail}
import com.anjunar.technologyspeaks.security.{ManagedPropertyFormResource, WebAuthnRegistrationResource}
import jakarta.validation.constraints.Email

import java.util.{Optional, UUID}

object UserSchema {

  def staticForService(builder: EntitySchemaBuilder[User]): EntitySchemaBuilder[User] = {
    builder
      .property("emails", property => property
        .forType(classOf[EMail], EMailSchema.static(_))
      )
      .property("info", property => property
        .forType(classOf[UserInfo], UserInfoSchema.staticCompact)
      )
  }

  def staticCompact(builder: EntitySchemaBuilder[User]): EntitySchemaBuilder[User] = {
    builder
        .property("id")
        .property("name")
  }


  def dynamic(builder: EntitySchemaBuilder[User], loaded: User): Unit = {
    val token = Credential.current()

    val currentUser = token.email.user

    val isOwnedOrAdmin = currentUser == loaded || token.hasRole("Administrator")
    val view = User.View.findByUser(loaded)

    builder
        .property("id")
        .property("name")
        .property("deleted")
        .property("emails", property => property
          .withManaged(name => manage(currentUser, isOwnedOrAdmin, view, name), (id, link) => {
            linkTo(methodOn(classOf[ManagedPropertyFormResource]).read(id))
              .withRel("secured")
              .build(link.addLink)
          })
          .forType(classOf[EMail], EMailSchema.static(_))
        )
        .property("enabled", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("info", property => property
          .withWriteable(isOwnedOrAdmin)
          .forType(classOf[UserInfo], UserInfoSchema.static(_, isOwnedOrAdmin))
        )
        .property("address", property => property
          .withWriteable(isOwnedOrAdmin)
          .forType(classOf[Address], AddressSchema.static(_, isOwnedOrAdmin))
        )

  }

  private def manage(currentUser: User, isOwnedOrAdmin: Boolean, view: EntityView, name: String) : (Boolean, UUID) = {
    val managedProperty = view.properties
      .stream()
      .filter(property => property.value == name)
      .findFirst()
      .orElseGet(() => {
        val property = new ManagedProperty()
        property.value = name
        property.view = view
        property.persist()
        view.properties.add(property)
        property
      })

    if (isOwnedOrAdmin) {
      (true, managedProperty.id)
    } else {
      if (managedProperty.visibleForAll) {
        (true, null)
      } else {
        (managedProperty.users.contains(currentUser) || managedProperty.groups.stream().anyMatch(group => group.users.contains(currentUser)), null)
      }
    }
  }

  def static(builder: EntitySchemaBuilder[User], isOwnedOrAdmin : Boolean): EntitySchemaBuilder[User] = {

    val currentUser = User.current()

    val view = User.View.findByUser(currentUser)

    builder
        .property("id")
        .property("name")
        .property("deleted")
        .property("emails", property => property
          .withManaged(name => manage(currentUser, isOwnedOrAdmin, view, name), (id, link) => {
            linkTo(methodOn(classOf[ManagedPropertyFormResource]).read(id))
              .withRel("secured")
              .build(link.addLink)
          })
          .forType(classOf[EMail], EMailSchema.static(_))
        )
        .property("enabled", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("info", property => property
          .withWriteable(isOwnedOrAdmin)
          .forType(classOf[UserInfo], UserInfoSchema.static(_, isOwnedOrAdmin))
        )
        .property("address", property => property
          .withWriteable(isOwnedOrAdmin)
          .forType(classOf[Address], AddressSchema.static(_, isOwnedOrAdmin))
        )
  }

}
