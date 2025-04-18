package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.UserInfo

object UserInfoSchema {

  def static(builder: SchemaBuilder, isOwnedOrAdmin: Boolean): Unit = {
    builder.forType(classOf[UserInfo], (entity: EntitySchemaBuilder[UserInfo]) => entity
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
  }

}
