package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.*
import com.anjunar.technologyspeaks.shared.RoleSchema

import java.lang.annotation.Annotation
import java.lang.reflect.Type

class RoleFormSchema extends EntityJSONSchema[Role] {
  override def build(root: Role, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    val credential = Credential.current()

    builder.forType(classOf[Role], RoleSchema.static(_, credential.hasRole("Administrator")))
  }
}

