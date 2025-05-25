package com.anjunar.technologyspeaks.control

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.QueryTable
import com.anjunar.technologyspeaks.shared.GroupSchema

import java.lang.reflect.Type

class GroupSearchSchema extends EntityJSONSchema[GroupSearch] {
  override def build(root: GroupSearch, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[GroupSearch], (builder: EntitySchemaBuilder[GroupSearch]) => builder
      .property("sort")
      .property("index")
      .property("limit")
      .property("name")
    )

    builder
  }

}