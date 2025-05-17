package com.anjunar.scala.mapper

import com.anjunar.scala.mapper.loader.EntityLoader
import com.anjunar.scala.schema.builder.{PropertyBuilder, SchemaBuilder}
import com.anjunar.scala.schema.model.Link
import jakarta.validation.{ConstraintViolation, Validator}

import java.util
import scala.collection.mutable

case class Context(parent : Context,
                   name : String,
                   noValidation : Boolean,
                   validator : Validator,
                   registry: ConverterRegistry,
                   schema: SchemaBuilder,
                   links : mutable.Buffer[Link],
                   loader: EntityLoader) {

  val children: mutable.Map[String, Context] = new mutable.HashMap[String, Context]()
  
  val violations : util.Set[ConstraintViolation[?]] = new util.HashSet[ConstraintViolation[?]]()

  var filter : Array[String] = Array()

}

object Context {

  def apply(parent : Context, propertyName: String, noValidation : Boolean, propertySchema : SchemaBuilder, context: Context): Context = {
    val newContext = Context(
      parent,
      propertyName,
      noValidation,
      context.validator,
      context.registry,
      propertySchema,
      context.links,
      context.loader
    )

    context.children.put(propertyName, newContext)

    newContext
  }

}