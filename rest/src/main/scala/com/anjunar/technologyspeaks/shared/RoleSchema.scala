package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, LinkContext, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{Credential, Role, User}

object RoleSchema {

  def static(builder: EntitySchemaBuilder[Role], isAdmin : Boolean): EntitySchemaBuilder[Role] = {
    builder
      .property("id")
      .property("name", property => property
        .withWriteable(isAdmin)
      )
      .property("description", property => property
        .withWriteable(isAdmin)
      )
  }


}
