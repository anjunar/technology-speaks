package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PropertyBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.media.{Media, Thumbnail}
import com.anjunar.technologyspeaks.shared.UserSchema

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class UserTableSchema extends EntityJSONSchema[Table[User]] {
  override def  build(root: Table[User], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(classOf[Table[User]], (builder: EntitySchemaBuilder[Table[User]]) => builder
      .property("rows", property => property
        .withTitle("Users")
        .forInstance(root.rows, classOf[User], (instance : User) => entity => UserSchema.dynamic(entity, instance))
      )
      .property("size")
    )
  }

}

