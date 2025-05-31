package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PrimitiveSchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.DocumentSchema

import java.lang.reflect.Type
import jakarta.persistence.Tuple

class DocumentTableSchema extends EntityJSONSchema[Table[Tuple]] {
  override def build(root: Table[Tuple], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[Table[Tuple]]) => builder
      .property("rows", property => property
        .withTitle("Documents")
        .forTuple(builder => builder
          .forPrimitive(classOf[Double], (builder : PrimitiveSchemaBuilder[Double]) => builder
            .withAlias("score")
            .withTitle("Score")
          )
          .forType(classOf[Document], builder => DocumentSchema.static(builder))
          .forInstance(root.rows, classOf[Document], (entity : Document) => builder => DocumentSchema.dynamic(builder, entity))
        )
      )
      .property("size")
    )
  }

}