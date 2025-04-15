package com.anjunar.scala.mapper

import com.anjunar.scala.mapper.intermediate.generator.JsonGenerator
import com.anjunar.scala.mapper.intermediate.model.{JsonNode, JsonObject}
import com.anjunar.scala.mapper.intermediate.parser.{Parser, Tokenizer}
import com.anjunar.scala.schema.builder.EntitySchemaBuilder
import com.anjunar.scala.schema.model.validators.SizeValidator
import com.anjunar.scala.schema.model.*
import com.anjunar.scala.universe.ResolvedClass

class JsonMapper {

  val registry = new ConverterRegistry

  def toJsonObjectForJson(jsonObject: JsonObject): String = {
    JsonGenerator.generate(jsonObject)
  }

  def toJson(entity: AnyRef, aType: ResolvedClass, context: Context): JsonObject = {
    val converter = registry.find(aType)
    val jsonObject = converter.toJson(entity, aType, context).asInstanceOf[JsonObject]
    jsonObject
  }

  def toJava(jsonNode: JsonNode, aType : ResolvedClass, context: Context): AnyRef = {
    val converter = registry.find(aType)
    converter.toJava(jsonNode, aType, context).asInstanceOf[AnyRef]
  }

  def toJsonObjectForJava(value: String) : JsonObject = {
    val tokens = Tokenizer.tokenize(value)
    val jsonNode = Parser.parse(tokens.listIterator())
    jsonNode.asInstanceOf[JsonObject]
  }
}
