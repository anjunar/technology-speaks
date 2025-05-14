package com.anjunar.technologyspeaks.security

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{Credential, User}
import com.anjunar.technologyspeaks.shared.CredentialSchema
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.{Consumes, POST, PUT, Path, Produces}

import java.lang.reflect.Type

class CredentialFormSchema extends EntityJSONSchema[Credential] {
  def build(root: Credential, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[Credential], CredentialSchema.dynamic(_, root))
  }
}

