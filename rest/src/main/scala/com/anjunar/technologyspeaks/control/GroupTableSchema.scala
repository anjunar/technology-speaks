package com.anjunar.technologyspeaks.control

import com.anjunar.scala.schema.builder.{EntityJSONSchema, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.shared.{GroupSchema, RoleSchema, TableSchema}

import java.lang.reflect.Type

class GroupTableSchema extends EntityJSONSchema[Table[Group]] {
  override def build(root: Table[Group], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    TableSchema.static(builder)
    GroupSchema.static(builder, false)

    builder
  }

}