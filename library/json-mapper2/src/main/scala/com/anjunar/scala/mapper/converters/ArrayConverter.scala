package com.anjunar.scala.mapper.converters

import com.anjunar.scala.mapper.Context
import com.anjunar.scala.mapper.intermediate.model.{JsonArray, JsonNode}
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

import java.util
import scala.jdk.CollectionConverters.*

class ArrayConverter extends AbstractConverter(TypeResolver.resolve(classOf[util.Collection[?]])) {

  override def toJson(instance: Any, aType: ResolvedClass, context: Context): JsonNode = instance match
    case collection: util.Collection[Any] =>
      JsonArray(
        collection.asScala.map(item => {
          val registry = context.registry
          val converter = registry.find(aType.typeArguments.head)
          converter.toJson(item, aType.typeArguments.head, context)
        })
      )
    case _ => throw new IllegalStateException("No Collection")

  override def toJava(jsonNode: JsonNode, aType: ResolvedClass, context: Context): Any = jsonNode match
    case array : JsonArray =>

      val collection: util.Collection[Any] = aType.raw match
        case set if classOf[util.Set[?]].isAssignableFrom(set) => new util.HashSet[Any]()
        case list if classOf[util.List[?]].isAssignableFrom(list) => new util.ArrayList[Any]()

      val registry = context.registry
      val converter = registry.find(aType.typeArguments.head)

      array.value.foreach(node => collection.add(converter.toJava(node, aType.typeArguments.head, context)))

      collection
    case _ => throw IllegalStateException("No JsonArray")
}
