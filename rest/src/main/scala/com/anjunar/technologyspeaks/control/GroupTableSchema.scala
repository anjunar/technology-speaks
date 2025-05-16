package com.anjunar.technologyspeaks.control

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.document.DocumentTableSearch
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.{GroupSchema, RoleSchema}

import java.lang.reflect.Type

class GroupTableSchema extends EntityJSONSchema[QueryTable[GroupTableSearch, Group]] {
  override def build(root: QueryTable[GroupTableSearch, Group], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[QueryTable[GroupTableSearch, Group]]) => builder
      .property("search", property => property
        .forType(classOf[GroupTableSearch], (builder: EntitySchemaBuilder[GroupTableSearch]) => builder
          .property("sort")
          .property("index")
          .property("limit")
          .property("name")
        )
      )
      .property("rows", property => property
        .withTitle("Groups")
        .forType(classOf[Group], builder => GroupSchema.static(builder))
        .forInstance(root.rows, classOf[Group], (entity : Group) => builder => GroupSchema.dynamic(builder, entity))
      )
      .property("size")
    )

    builder
  }

}