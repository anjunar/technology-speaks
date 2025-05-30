package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PrimitiveSchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table, TupleTable}
import com.anjunar.technologyspeaks.shared.DocumentSchema

import java.lang.reflect.Type

class DocumentTableSchema extends EntityJSONSchema[TupleTable[Document]] {
  override def build(root: TupleTable[Document], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[TupleTable[Document]]) => builder
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