package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.EntitySchemaBuilder
import com.anjunar.technologyspeaks.control.{Credential, Role}

object CredentialSchema {

  def static(builder: EntitySchemaBuilder[Credential]): EntitySchemaBuilder[Credential] = {

    val credential = Credential.current()

    builder
      .property("id")
      .property("displayName")
      .property("roles", property => property
        .forType(classOf[Role], RoleSchema.static(_, credential.hasRole("Administrator")))
      )
  }


}
