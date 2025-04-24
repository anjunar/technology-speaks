package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.EntitySchemaBuilder
import com.anjunar.technologyspeaks.control.{Credential, Role, RoleTableResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}

object CredentialSchema {

  def static(builder: EntitySchemaBuilder[Credential], loaded : Credential): EntitySchemaBuilder[Credential] = {

    val credential = Credential.current()
    val isAdmin = credential.hasRole("Administrator")

    builder
      .property("id")
      .property("displayName")
      .property("roles", property => property
        .withWriteable(isAdmin)
        .withLinks(links => {
          linkTo(methodOn(classOf[RoleTableResource]).list(null))
            .build(links.addLink)
        })
        .forInstance(loaded.roles, classOf[Role], (entity : Role) => builder => RoleSchema.static(builder, entity))
      )
  }


}
