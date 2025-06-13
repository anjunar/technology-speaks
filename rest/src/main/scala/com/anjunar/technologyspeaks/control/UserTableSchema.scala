package com.anjunar.technologyspeaks.control

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PrimitiveSchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.shared.UserSchema
import jakarta.persistence.Tuple

import java.lang.reflect.Type


class UserTableSchema extends EntityJSONSchema[Table[Tuple]] {
  override def build(root: Table[Tuple], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[Table[Tuple]]) => builder
      .property("rows", property => property
        .withTitle("Users")
        .forTuple(builder => builder
          .forPrimitive(classOf[Double], (builder: PrimitiveSchemaBuilder[Double]) => builder
            .withAlias("score")
            .withTitle("Score")
          )
          .forType(classOf[User], builder => UserSchema.static(builder))
          .forInstance(root.rows, classOf[User], (instance: User) => entity => UserSchema.dynamic(entity, instance))
        )
      )
      .property("size")
    )
  }

}

