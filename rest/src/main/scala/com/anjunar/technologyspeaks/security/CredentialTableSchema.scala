package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{Credential, EMail, Role, User, UserFormResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.shared.{CredentialSchema, RoleSchema, UserSchema}

import java.lang.reflect.Type

class CredentialTableSchema extends EntityJSONSchema[Table[Credential]] {
  override def  build(root: Table[Credential], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[Table[Credential]]) => builder
      .property("rows", property => property
        .withTitle("Credentials")
        .forType(classOf[Credential], builder => CredentialSchema.static(builder))
        .forInstance(root.rows, classOf[Credential], (entity : Credential) => builder => CredentialSchema.dynamic(builder, entity))
      )
      .property("size")
    )
  }

}

