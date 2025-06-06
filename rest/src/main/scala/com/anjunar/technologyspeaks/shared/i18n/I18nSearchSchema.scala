package com.anjunar.technologyspeaks.shared.i18n

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}

import java.lang.reflect.Type

class I18nSearchSchema extends EntityJSONSchema[I18nSearch] {
  override def build(root: I18nSearch, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[I18nSearch], (builder: EntitySchemaBuilder[I18nSearch]) => builder
      .property("sort")
      .property("index")
      .property("limit")
    )

  }

}