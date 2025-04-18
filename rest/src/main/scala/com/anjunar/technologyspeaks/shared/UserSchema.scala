package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, LinkContext, PropertyBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.*
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{createProxy, linkTo, methodOn}
import com.anjunar.technologyspeaks.media.{Media, Thumbnail}
import com.anjunar.technologyspeaks.security.{ManagedPropertyFormResource, WebAuthnRegistrationResource}

import java.util.{Optional, UUID}

object UserSchema {

  def staticForService(builder: SchemaBuilder): Unit = {
    builder
      .forType(classOf[User], (entity: EntitySchemaBuilder[User]) => entity
        .property("emails")
        .property("info")
      )
      .forType(classOf[UserInfo], entity => entity
        .property("firstName")
        .property("lastName")
      )

    EMailSchema.static(builder)
  }

  def staticCompact(builder: SchemaBuilder, isOwnedOrAdmin : Boolean): SchemaBuilder = {

    builder
      .forType(classOf[User], (entity: EntitySchemaBuilder[User]) => entity
        .property("id")
        .property("emails")
      )

    builder

  }


  def dynamic(builder: SchemaBuilder, loaded: User): Unit = {
    val token = Credential.current()

    val currentUser = token.email.user

    val isOwnedOrAdmin = currentUser == loaded || token.hasRole("Administrator")
    val view = User.View.findByUser(loaded)

    builder
      .forInstance(loaded, classOf[User], (entity: EntitySchemaBuilder[User]) => entity
        .property("id")
        .property("deleted")
        .property("emails", property => property
          .withManaged(name => manage(currentUser, isOwnedOrAdmin, view, name), (id, link) => {
            linkTo(methodOn(classOf[ManagedPropertyFormResource]).read(id))
              .withRel("secured")
              .build(link.addLink)
          })
        )
        .property("enabled", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("info", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("address", property => property
          .withWriteable(isOwnedOrAdmin)
        )
      )

    ManagedPropertySchema.static(builder, isOwnedOrAdmin)
    EMailSchema.static(builder)
    UserInfoSchema.static(builder, isOwnedOrAdmin)
    AddressSchema.static(builder, isOwnedOrAdmin)
    GeoPointSchema.static(builder, isOwnedOrAdmin)
    MediaSchema.static(builder, isOwnedOrAdmin)
    ThumbnailSchema.static(builder, isOwnedOrAdmin)
    RoleSchema.static(builder)
    
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

  def static(builder: SchemaBuilder, isOwnedOrAdmin : Boolean): SchemaBuilder = {

    val currentUser = User.current()

    val view = User.View.findByUser(currentUser)

    builder
      .forType(classOf[User], (entity: EntitySchemaBuilder[User]) => entity
        .property("id")
        .property("deleted")
        .property("emails", property => property
          .withManaged(name => manage(currentUser, isOwnedOrAdmin, view, name), (id, link) => {
            linkTo(methodOn(classOf[ManagedPropertyFormResource]).read(id))
              .withRel("secured")
              .build(link.addLink)
          })
        )
        .property("enabled", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("info", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("address", property => property
          .withWriteable(isOwnedOrAdmin)
        )
      )

    ManagedPropertySchema.static(builder, isOwnedOrAdmin)
    EMailSchema.static(builder)
    UserInfoSchema.static(builder, isOwnedOrAdmin)
    AddressSchema.static(builder, isOwnedOrAdmin)
    GeoPointSchema.static(builder, isOwnedOrAdmin)
    MediaSchema.static(builder, isOwnedOrAdmin)
    ThumbnailSchema.static(builder, isOwnedOrAdmin)
    RoleSchema.static(builder)

    builder

  }

}
