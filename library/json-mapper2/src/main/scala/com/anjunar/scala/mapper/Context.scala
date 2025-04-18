package com.anjunar.scala.mapper

import com.anjunar.scala.mapper.loader.EntityLoader
import com.anjunar.scala.schema.builder.{PropertyBuilder, SchemaBuilder}
import jakarta.validation.{ConstraintViolation, Validator}

import java.util
import scala.collection.mutable

case class Context(name : String,
                   validator : Validator,
                   registry: ConverterRegistry,
                   schema: SchemaBuilder,
                   loader: EntityLoader) {

  val children: mutable.Map[String, Context] = new mutable.HashMap[String, Context]()
  
  val violations : util.Set[ConstraintViolation[?]] = new util.HashSet[ConstraintViolation[?]]()

}

object Context {

  def apply(propertyName: String, propertySchema : SchemaBuilder, context: Context): Context = {
    val newContext = Context(
      propertyName,
      context.validator,
      context.registry,
      propertySchema,
      context.loader
    )

    context.children.put(propertyName, newContext)

    newContext
  }

}