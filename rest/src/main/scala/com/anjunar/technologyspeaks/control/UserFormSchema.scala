package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PropertyBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{createProxy, linkTo, methodOn}
import com.anjunar.technologyspeaks.media.{Media, Thumbnail}
import com.anjunar.technologyspeaks.shared.UserSchema

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class UserFormSchema extends EntityJSONSchema[User] {
  override def build(root: User, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    val credential = Credential.current()
    val current = credential.email.user
    val isOwnedOrAdmin = current == root.owner || credential.hasRole("Administrator")

    UserSchema.static(builder, isOwnedOrAdmin)

    builder
  }
}

