package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.shared.HashTagSchema
import com.anjunar.technologyspeaks.shared.hashtag.HashTag

import java.lang.reflect.Type

class HashTagTableSchema extends EntityJSONSchema[Table[HashTag]] {
  override def build(root: Table[HashTag], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[Table[HashTag]]) => builder
      .property("rows", property => property
        .withTitle("Hash Tags")
        .forType(classOf[HashTag], builder => HashTagSchema.static(builder))
      )
      .property("size")
    )
  }

}