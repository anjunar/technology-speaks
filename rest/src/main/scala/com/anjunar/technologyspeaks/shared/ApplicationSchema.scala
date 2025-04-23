package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, LinkContext, SchemaBuilder}
import com.anjunar.technologyspeaks.Application
import com.anjunar.technologyspeaks.control.User

object ApplicationSchema {

  def read(builder: EntitySchemaBuilder[Application], isOwnedOrAdmin : Boolean): EntitySchemaBuilder[Application] = {
    builder.property("user", property => property
      .forType(classOf[User], UserSchema.staticForService(_, isOwnedOrAdmin))
    )
  }

}
