package com.anjunar.scala.mapper.converters

import com.anjunar.scala.mapper.Context
import com.anjunar.scala.mapper.intermediate.model.{JsonNode, JsonString}
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

import java.util.Base64

class ByteConverter extends AbstractConverter(TypeResolver.resolve(classOf[Array[Byte]])) {

  override def toJson(instance: Any, aType: ResolvedClass, context: Context): JsonNode = JsonString(Base64.getEncoder.encodeToString(instance.asInstanceOf[Array[Byte]]))

  override def toJava(jsonNode: JsonNode, aType: ResolvedClass, context: Context): Any = Base64.getDecoder.decode(jsonNode.value.asInstanceOf[String].getBytes)
  
}
