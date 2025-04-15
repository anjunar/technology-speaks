package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.media.{Media, Thumbnail}
import com.anjunar.technologyspeaks.shared.{TableSchema, UserSchema}
import com.anjunar.scala.mapper.annotations.{Action, JsonSchema}
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PropertyBuilder, SchemaBuilder}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class UserTableSchema extends EntityJSONSchema[Table[User]] {
  override def  build(root: Table[User], javaType: Type, action: JsonSchema.State): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    TableSchema.static(builder)
    
    root.rows.forEach(user => UserSchema.dynamic(builder, user, action))

    builder.forType(classOf[Table[User]], (entity : EntitySchemaBuilder[Table[User]]) => entity
      .withLinks((instance, link) => {
        linkTo(methodOn(classOf[UserFormResource]).create)
          .build(link.addLink)
      })
    )
    
    builder.forType(classOf[User], (entity : EntitySchemaBuilder[User]) => entity
      .withLinks((row, link) => {
        linkTo(methodOn(classOf[UserFormResource]).read(row.id))
          .build(link.addLink)
      })
    )

    builder
  }

}

