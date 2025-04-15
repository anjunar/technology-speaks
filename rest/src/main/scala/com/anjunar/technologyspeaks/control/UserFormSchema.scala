package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{createProxy, linkTo, methodOn}
import com.anjunar.technologyspeaks.media.{Media, Thumbnail}
import com.anjunar.technologyspeaks.shared.UserSchema
import com.anjunar.scala.mapper.annotations.JsonSchema.State
import com.anjunar.scala.mapper.annotations.{Action, JsonSchema}
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PropertyBuilder, SchemaBuilder}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class UserFormSchema extends EntityJSONSchema[User] {
  override def build(root: User, javaType: Type, action: State): SchemaBuilder = {
    val builder = new SchemaBuilder()

    val credential = Credential.current()
    val current = credential.email.user
    val isOwnedOrAdmin = current == root.owner || credential.hasRole("Administrator")

    UserSchema.static(builder, action, isOwnedOrAdmin)
    
    builder.forType(classOf[User], (entity : EntitySchemaBuilder[User]) => entity
      .withLinks( (user, link) => {
        action match
          case State.ENTRYPOINT =>
            linkTo(methodOn(classOf[UserFormResource]).save(null))
              .build(link.addLink)
          case State.READ | State.UPDATE | State.CREATE =>
            linkTo(methodOn(classOf[UserFormResource]).update(user))
              .build(link.addLink)
            linkTo(methodOn(classOf[UserFormResource]).delete(user))
              .build(link.addLink)
          case State.DELETE =>
            linkTo(methodOn(classOf[UserTableResource]).list(null))
              .withRedirect
              .build(link.addLink)

      })
    )
    
    builder
  }
}

