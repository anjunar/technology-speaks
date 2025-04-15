package com.anjunar.technologyspeaks.shared

import com.anjunar.technologyspeaks.control.{Role, Credential, User}
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, LinkContext, SchemaBuilder}

object RoleSchema {

  def static(builder: SchemaBuilder): Unit = {
    val current = Credential.current()
    val isAdmin = current.hasRole("Administrator")
    
    builder
      .forType(classOf[Role], (entity: EntitySchemaBuilder[Role]) => entity
        .property("id")
        .property("name", property => property
          .withWriteable(isAdmin)
        )
        .property("description", property => property
          .withWriteable(isAdmin)
        )
      )
  }


}
