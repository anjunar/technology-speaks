package com.anjunar.scala.mapper

import com.anjunar.scala.mapper.loader.EntityLoader
import com.anjunar.scala.schema.builder.SchemaBuilder
import jakarta.validation.{ConstraintViolation, Validator}

import java.util
import scala.collection.mutable

case class Context(validator : Validator,
                   registry: ConverterRegistry,
                   schema: SchemaBuilder,
                   loader: EntityLoader) {

  val children: mutable.Map[String, Context] = new mutable.HashMap[String, Context]()
  
  val violations : util.Set[ConstraintViolation[?]] = new util.HashSet[ConstraintViolation[?]]()

}

object Context {

  def apply(propertyName: String, context: Context): Context = {
    val newContext = Context(
      context.validator,
      context.registry,
      context.schema,
      context.loader
    )

    context.children.put(propertyName, newContext)

    newContext
  }

}