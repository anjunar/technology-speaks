package com.anjunar.scala.mapper.converters

import com.anjunar.scala.mapper.Context
import com.anjunar.scala.mapper.intermediate.model.{JsonNode, JsonString}
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

class StringConverter extends AbstractConverter(TypeResolver.resolve(classOf[String])) {

  override def toJson(instance: Any, aType: ResolvedClass, context: Context): JsonNode = JsonString(instance.asInstanceOf[String])

  override def toJava(jsonNode: JsonNode, aType: ResolvedClass, context: Context): Any = jsonNode.value
  
}
