package com.anjunar.scala.mapper.converters

import com.anjunar.scala.mapper.Context
import com.anjunar.scala.mapper.intermediate.model.{JsonNode, JsonString}
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

import java.time.Duration
import java.time.temporal.TemporalAmount

class TemporalAmountConverter extends AbstractConverter(TypeResolver.resolve(classOf[TemporalAmount])) {

  override def toJson(instance: Any, aType: ResolvedClass, context: Context): JsonNode = instance match
    case temporal : TemporalAmount => JsonString(temporal.toString)

  override def toJava(jsonNode: JsonNode, aType: ResolvedClass, context: Context): Any = {
    Duration.parse(jsonNode.value.toString)
  }
}
