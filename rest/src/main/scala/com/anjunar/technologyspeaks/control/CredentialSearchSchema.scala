package com.anjunar.technologyspeaks.control

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.shared.{CredentialSchema, RoleSchema}

import java.lang.reflect.Type

class CredentialSearchSchema extends EntityJSONSchema[CredentialSearch] {
  override def build(root: CredentialSearch, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[CredentialSearch], (builder: EntitySchemaBuilder[CredentialSearch]) => builder
      .property("sort")
      .property("index")
      .property("limit")
      .property("displayName")
      .property("roles", property => property
        .forType(classOf[Role], RoleSchema.static)
        .withLinks(links => {
          linkTo(methodOn(classOf[RoleTableResource]).list(null))
            .build(links.addLink)
        })
      )
    )
  }

}

