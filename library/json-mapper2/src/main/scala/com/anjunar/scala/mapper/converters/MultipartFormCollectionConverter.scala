package com.anjunar.scala.mapper.converters

import com.anjunar.scala.mapper.MultipartFormContext
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

class MultipartFormCollectionConverter extends MultipartFormAbstractConverter(TypeResolver.resolve(classOf[java.util.Collection[?]])) {

  override def toJava(values: List[String], aType: ResolvedClass, context: MultipartFormContext): Any = {
    values.map(value => context.loader.load(Map(("id" -> List(value))), aType.typeArguments.head, Array()))
  }

}
