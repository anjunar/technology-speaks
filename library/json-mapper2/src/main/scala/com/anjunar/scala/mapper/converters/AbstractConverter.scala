package com.anjunar.scala.mapper.converters

import com.anjunar.scala.mapper.Context
import com.anjunar.scala.mapper.intermediate.model.{JsonNode, JsonObject}
import com.anjunar.scala.universe.ResolvedClass

abstract class AbstractConverter(val aClass : ResolvedClass) {
  
  def toJson(instance: Any, aType: ResolvedClass, context: Context): JsonNode
  
  def toJava(jsonNode: JsonNode, aType: ResolvedClass, context: Context): Any
  
}
