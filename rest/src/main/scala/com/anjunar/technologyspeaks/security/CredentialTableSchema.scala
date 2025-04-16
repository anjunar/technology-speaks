package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{Credential, EMail, User, UserFormResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.shared.{RoleSchema, TableSchema, UserSchema}

import java.lang.reflect.Type

class CredentialTableSchema extends EntityJSONSchema[Table[Credential]] {
  override def  build(root: Table[Credential], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    TableSchema.static(builder)
    RoleSchema.static(builder)

    root.rows.forEach(row => builder.forInstance(row, classOf[Credential], (entity : EntitySchemaBuilder[Credential]) => entity
      .property("displayName")
      .property("email")
      .property("roles")
    ))

    builder.forType(classOf[EMail], entity => entity
      .property("value")
    )

    builder
  }

}

