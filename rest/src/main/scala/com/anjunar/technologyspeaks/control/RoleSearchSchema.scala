package com.anjunar.technologyspeaks.control

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.QueryTable
import com.anjunar.technologyspeaks.shared.RoleSchema

import java.lang.reflect.Type

class RoleSearchSchema extends EntityJSONSchema[RoleSearch] {
  override def build(root: RoleSearch, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[RoleSearch], (builder: EntitySchemaBuilder[RoleSearch]) => builder
      .property("sort")
      .property("index")
      .property("limit")
      .property("name")
      .property("description")
    )


  }

}