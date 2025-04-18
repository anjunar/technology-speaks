package com.anjunar.technologyspeaks.control

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.shared.{GroupSchema, RoleSchema}

import java.lang.reflect.Type

class GroupTableSchema extends EntityJSONSchema[Table[Group]] {
  override def build(root: Table[Group], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[Table[Group]], (builder: EntitySchemaBuilder[Table[Group]]) => builder
      .property("rows", property => property
        .withTitle("Groups")
        .forType(classOf[Group], GroupSchema.static(_, false))
      )
      .property("size")
    )

    builder
  }

}