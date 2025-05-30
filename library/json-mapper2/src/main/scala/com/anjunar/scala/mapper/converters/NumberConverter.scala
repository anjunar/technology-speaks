package com.anjunar.scala.mapper.converters

import com.anjunar.scala.mapper.Context
import com.anjunar.scala.mapper.intermediate.model.{JsonNode, JsonNumber}
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

class NumberConverter extends AbstractConverter(TypeResolver.resolve(classOf[Number])) {
  
  override def toJson(instance: Any, aType: ResolvedClass, context: Context): JsonNode = JsonNumber(instance.toString)

  override def toJava(jsonNode: JsonNode, aType: ResolvedClass, context: Context): Any = aType.raw match
    case bigDecimal if bigDecimal == classOf[java.math.BigDecimal] =>
      val constructor = bigDecimal.getConstructor(classOf[String])
      constructor.newInstance(jsonNode.value)
    case bigInteger if bigInteger == classOf[java.math.BigInteger] =>
      val constructor = bigInteger.getConstructor(classOf[String])
      constructor.newInstance(jsonNode.value)
    case _ =>
      val method = aType.findDeclaredMethod("valueOf", classOf[String])
      method.invoke(null, jsonNode.value)
}
