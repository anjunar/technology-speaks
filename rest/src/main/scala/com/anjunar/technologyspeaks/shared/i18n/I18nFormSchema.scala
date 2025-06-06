package com.anjunar.technologyspeaks.shared.i18n

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.shared.I18nSchema

import java.lang.reflect.Type


class I18nFormSchema extends EntityJSONSchema[I18n] {
  override def build(root: I18n, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[I18n], I18nSchema.static)
  }
}

