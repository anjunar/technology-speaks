package com.anjunar.technologyspeaks.control

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.QueryTable
import com.anjunar.technologyspeaks.shared.UserSchema

import java.lang.reflect.Type

class UserSearchSchema extends EntityJSONSchema[UserSearch] {
  override def build(root: UserSearch, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[UserSearch], (builder: EntitySchemaBuilder[UserSearch]) => builder
      .property("sort")
      .property("index")
      .property("limit")
      .property("nickName")
      .property("firstName")
      .property("lastName")
      .property("email")
      .property("birthDate")
    )

  }

}

