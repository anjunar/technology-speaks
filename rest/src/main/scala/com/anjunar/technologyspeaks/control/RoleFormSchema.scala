package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.*
import com.anjunar.technologyspeaks.shared.RoleSchema
import com.anjunar.scala.mapper.annotations.JsonSchema.State
import com.anjunar.scala.mapper.annotations.{Action, JsonSchema}
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}

import java.lang.annotation.Annotation
import java.lang.reflect.Type

class RoleFormSchema extends EntityJSONSchema[Role] {
  override def build(root: Role, javaType: Type, action: State): SchemaBuilder = {
    val builder = new SchemaBuilder()
    
    RoleSchema.static(builder)
    
    builder.forType(classOf[Role], (entity : EntitySchemaBuilder[Role]) => entity
      .withLinks((instance, link) => {
        action match
          case State.ENTRYPOINT =>
            linkTo(methodOn(classOf[RoleFormResource]).save(null))
              .build(link.addLink)
          case State.READ | State.UPDATE | State.CREATE =>
            linkTo(methodOn(classOf[RoleFormResource]).update(null))
              .build(link.addLink)
            linkTo(methodOn(classOf[RoleFormResource]).delete(instance))
              .build(link.addLink)
            linkTo(methodOn(classOf[RoleTableResource]).list(null))
              .withRedirect
              .build(link.addLink)
          case State.DELETE =>
            linkTo(methodOn(classOf[RoleTableResource]).list(null))
              .withRedirect
              .build(link.addLink)
      })
    )
    
    builder
  }
}

