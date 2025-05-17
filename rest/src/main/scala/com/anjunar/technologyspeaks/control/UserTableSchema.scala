package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PropertyBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.media.{Media, Thumbnail}
import com.anjunar.technologyspeaks.shared.UserSchema

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class UserTableSchema extends EntityJSONSchema[QueryTable[UserTableSearch, User]] {
  override def  build(root: QueryTable[UserTableSearch, User], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[QueryTable[UserTableSearch, User]]) => builder
      .property("search", property => property
        .forType(classOf[UserTableSearch], (builder: EntitySchemaBuilder[UserTableSearch]) => builder
          .property("sort")
          .property("index")
          .property("limit")
          .property("email")
          .property("birthDate")
        )
      )
      .property("rows", property => property
        .withTitle("Users")
        .forType(classOf[User], builder => UserSchema.static(builder))
        .forInstance(root.rows, classOf[User], (instance : User) => entity => UserSchema.dynamic(entity, instance))
      )
      .property("size")
    )
  }

}

