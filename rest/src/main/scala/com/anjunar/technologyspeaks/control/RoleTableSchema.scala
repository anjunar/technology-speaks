package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.shared.{GroupSchema, RoleSchema}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class RoleTableSchema extends EntityJSONSchema[Table[Role]] {
  override def build(root: Table[Role], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[Table[Role]], (builder: EntitySchemaBuilder[Table[Role]]) => builder
      .property("rows", property => property
        .withTitle("Roles")
        .forInstance(root.rows, classOf[Role], (entity : Role) => builder => RoleSchema.static(builder, entity))
      )
      .property("size")
    )
  }

}