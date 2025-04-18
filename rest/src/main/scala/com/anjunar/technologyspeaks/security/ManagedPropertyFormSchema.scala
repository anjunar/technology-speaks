package com.anjunar.technologyspeaks.security

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{Credential, User}
import com.anjunar.technologyspeaks.shared.{ManagedProperty, ManagedPropertySchema}
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.{Consumes, POST, PUT, Path, Produces}

import java.lang.reflect.Type

class ManagedPropertyFormSchema extends EntityJSONSchema[ManagedProperty] {
  def build(root: ManagedProperty, javaType: Type): SchemaBuilder = {

    val current = User.current()
    val isOwnedOrAdmin = current == root.view.owner || Credential.current().hasRole("Administrator")


    ManagedPropertySchema.static(new SchemaBuilder(), isOwnedOrAdmin)
  }
}

