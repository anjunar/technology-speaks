package com.anjunar.scala.mapper.converters

import com.anjunar.scala.mapper.Context
import com.anjunar.scala.mapper.intermediate.model.{JsonBoolean, JsonNode}
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

import java.lang.Boolean

class BooleanConverter extends AbstractConverter(TypeResolver.resolve(classOf[Boolean])) {

  override def toJson(instance: Any, aType: ResolvedClass, context: Context): JsonNode = JsonBoolean(instance.asInstanceOf[Boolean])

  override def toJava(jsonObject: JsonNode, aType: ResolvedClass, context: Context): Any = jsonObject.value
  
}
