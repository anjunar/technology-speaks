package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, LinkContext, PropertyBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.*
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.media.{Media, Thumbnail}
import com.anjunar.technologyspeaks.security.WebAuthnRegistrationResource

object UserSchema {

  def staticForService(builder: SchemaBuilder): Unit = builder
    .forType(classOf[User], (entity: EntitySchemaBuilder[User]) => entity
      .property("emails")
      .property("info")
    )
    .forType(classOf[UserInfo], entity => entity
      .property("firstName")
      .property("lastName")
    )
    .forType(classOf[EMail], (entity: EntitySchemaBuilder[EMail]) => entity
      .property("value")
    )


  def staticForSelect(builder: SchemaBuilder): SchemaBuilder = builder
    .forType(classOf[User], entity => {
      entity
        .property("id")
        .property("emails")
        .property("info")
        .property("address")
    })
    .forType(classOf[EMail], (entity: EntitySchemaBuilder[EMail]) => entity
      .property("value")
    )
    .forType(classOf[UserInfo], entity => entity
      .property("id")
      .property("firstName")
      .property("lastName")
    )
    .forType(classOf[Address], entity => entity
      .property("id")
      .property("street")
      .property("number")
      .property("zipCode")
      .property("country")
    )

  def dynamic(builder: SchemaBuilder, loaded: User): Unit = {
    val token = Credential.current()

    val isOwnedOrAdmin = token.email.user == loaded || token.hasRole("Administrator")

    builder
      .forInstance(loaded, classOf[User], (entity: EntitySchemaBuilder[User]) => entity
        .property("id")
        .property("deleted")
        .property("emails")
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
      .forType(classOf[EMail], (entity: EntitySchemaBuilder[EMail]) => entity
        .property("value")
      )

    addUserData(builder, isOwnedOrAdmin)
    
    RoleSchema.static(builder)
    
  }

  def static(builder: SchemaBuilder, isOwnedOrAdmin : Boolean): Unit = {

    builder
      .forType(classOf[User], (entity: EntitySchemaBuilder[User]) => entity
        .property("id")
        .property("deleted")
        .property("emails")
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
      .forType(classOf[EMail], (entity: EntitySchemaBuilder[EMail]) => entity
        .property("value")
      )

    addUserData(builder, isOwnedOrAdmin)

    RoleSchema.static(builder)

  }

  private def addUserData(builder: SchemaBuilder, isOwnedOrAdmin: Boolean) = {
    builder
      .forType(classOf[UserInfo], (entity: EntitySchemaBuilder[UserInfo]) => entity
        .property("id")
        .property("firstName", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("lastName", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("image", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("birthDate", property => property
          .withWriteable(isOwnedOrAdmin)
        )
      )
      .forType(classOf[Address], (entity: EntitySchemaBuilder[Address]) => entity
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
      .forType(classOf[GeoPoint], (entity: EntitySchemaBuilder[GeoPoint]) => entity
        .property("x")
        .property("y")
      )
      .forType(classOf[Media], (entity: EntitySchemaBuilder[Media]) => entity
        .property("id")
        .property("name", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("type", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("subType", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("data", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("thumbnail", property => property
          .withWriteable(isOwnedOrAdmin)
        )
      )
      .forType(classOf[Thumbnail], (entity: EntitySchemaBuilder[Thumbnail]) => entity
        .property("id")
        .property("name", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("name", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("type", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("subType", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("data", property => property
          .withWriteable(isOwnedOrAdmin)
        )
      )
  }

}
