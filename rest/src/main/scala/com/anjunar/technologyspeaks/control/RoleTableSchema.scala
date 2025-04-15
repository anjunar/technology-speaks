package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.shared.{RoleSchema, TableSchema}
import com.anjunar.scala.mapper.annotations.{Action, JsonSchema}
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class RoleTableSchema extends EntityJSONSchema[Table[Role]] {
  override def build(root: Table[Role], javaType: Type, action: JsonSchema.State): SchemaBuilder = {
    val builder = new SchemaBuilder()

    TableSchema.static(builder)
    RoleSchema.static(builder)
    
    builder.forType(classOf[Table[Role]], (entity : EntitySchemaBuilder[Table[Role]]) => entity
      .withLinks((instance, link) => {
        linkTo(methodOn(classOf[RoleFormResource]).create)
          .build(link.addLink)
      })
    )
    
    builder.forType(classOf[Role], (entity : EntitySchemaBuilder[Role]) => entity
      .withLinks((row, link) => {
        linkTo(methodOn(classOf[RoleFormResource]).read(row.id))
          .build(link.addLink)
      })
    )

    builder
  }

}