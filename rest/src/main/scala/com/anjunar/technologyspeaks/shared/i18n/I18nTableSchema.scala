package com.anjunar.technologyspeaks.shared.i18n

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.RoleSearch
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.I18nSchema

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class I18nTableSchema extends EntityJSONSchema[Table[I18n]] {
  override def build(root: Table[I18n], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[Table[I18n]]) => builder
      .property("rows", property => property
        .withTitle("I18ns")
        .forType(classOf[I18n], builder => I18nSchema.static(builder))
        .forInstance(root.rows, classOf[I18n], (entity : I18n) => (builder  : EntitySchemaBuilder[I18n]) => I18nSchema.static(builder))
      )
      .property("size")
    )
  }

}