package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.EntitySchemaBuilder
import com.anjunar.technologyspeaks.control.{Credential, Role}

object CredentialSchema {

  def static(builder: EntitySchemaBuilder[Credential], loaded : Credential): EntitySchemaBuilder[Credential] = {
    builder
      .property("id")
      .property("displayName")
      .property("roles", property => property
        .forInstance(loaded.roles, classOf[Role], (entity : Role) => builder => RoleSchema.static(builder, entity))
      )
  }


}
