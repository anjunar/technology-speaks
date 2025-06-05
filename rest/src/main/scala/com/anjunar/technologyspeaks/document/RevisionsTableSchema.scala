package com.anjunar.technologyspeaks.document

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PrimitiveSchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.{DocumentSchema, RevisionSchema}
import jakarta.persistence.Tuple

import java.lang.reflect.Type

class RevisionsTableSchema extends EntityJSONSchema[Table[Revision]] {
  override def build(root: Table[Revision], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[Table[Revision]]) => builder
      .property("rows", property => property
        .withTitle("Documents")
        .forInstance(root.rows, classOf[Revision], entity => builder => RevisionSchema.static(builder))
        .forType(classOf[Revision], builder => RevisionSchema.static(builder))
      )
      .property("size")
    )
  }

}