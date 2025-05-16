package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.{GroupSchema, RoleSchema}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class RoleTableSchema extends EntityJSONSchema[QueryTable[RoleTableSearch, Role]] {
  override def build(root: QueryTable[RoleTableSearch, Role], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[QueryTable[RoleTableSearch, Role]]) => builder
      .property("search", property => property
        .forType(classOf[RoleTableSearch], (builder: EntitySchemaBuilder[RoleTableSearch]) => builder
          .property("sort")
          .property("index")
          .property("limit")
          .property("name")
          .property("description")
        )
      )
      .property("rows", property => property
        .withTitle("Roles")
        .forType(classOf[Role], builder => RoleSchema.static(builder))
        .forInstance(root.rows, classOf[Role], (entity : Role) => builder => RoleSchema.dynamic(builder, entity))
      )
      .property("size")
    )
  }

}