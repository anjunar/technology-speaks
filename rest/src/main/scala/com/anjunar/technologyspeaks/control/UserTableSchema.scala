package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PropertyBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.media.{Media, Thumbnail}
import com.anjunar.technologyspeaks.shared.{TableSchema, UserSchema}

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class UserTableSchema extends EntityJSONSchema[Table[User]] {
  override def  build(root: Table[User], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    TableSchema.static(builder)
    
    root.rows.forEach(user => UserSchema.dynamic(builder, user))

    builder
  }

}

