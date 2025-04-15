package com.anjunar.scala.mapper.converters

import com.anjunar.scala.mapper.Context
import com.anjunar.scala.mapper.intermediate.model.{JsonNode, JsonString}
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

import java.util.UUID

class UUIDConverter extends AbstractConverter(TypeResolver.resolve(classOf[UUID])) {

  override def toJson(instance: Any, aType: ResolvedClass, context: Context): JsonNode = instance match
    case uuid : UUID => JsonString(uuid.toString)

  override def toJava(jsonNode: JsonNode, aType: ResolvedClass, context: Context): Any = {
    UUID.fromString(jsonNode.value.asInstanceOf[String])
  }
  
}
