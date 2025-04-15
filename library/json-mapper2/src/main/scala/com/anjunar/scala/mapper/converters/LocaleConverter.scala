package com.anjunar.scala.mapper.converters

import com.anjunar.scala.mapper.Context
import com.anjunar.scala.mapper.intermediate.model.{JsonNode, JsonString}
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

import java.util.Locale

class LocaleConverter extends AbstractConverter(TypeResolver.resolve(classOf[Locale])) {

  override def toJson(instance: Any, aType: ResolvedClass, context: Context): JsonNode = JsonString(instance.asInstanceOf[Locale].toLanguageTag)

  override def toJava(jsonNode: JsonNode, aType: ResolvedClass, context: Context): Any = Locale.forLanguageTag(jsonNode.value.asInstanceOf[String])
}
