package com.anjunar.scala.mapper.converters

import com.anjunar.scala.mapper.annotations.IgnoreFilter
import com.anjunar.scala.mapper.intermediate.model.{JsonNode, JsonObject, JsonString}
import com.anjunar.scala.mapper.{Context, ConverterRegistry}
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, LinkContext, PropertyBuilder, SchemaBuilder}
import com.anjunar.scala.schema.model.{Link, Links, NodeDescriptor}
import com.anjunar.scala.universe.introspector.{BeanIntrospector, BeanProperty}
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.google.common.reflect.TypeToken
import com.typesafe.scalalogging.Logger

import java.lang.reflect.Type
import java.util
import scala.collection.mutable
import scala.jdk.CollectionConverters.*


class BeanConverter extends AbstractConverter(TypeResolver.resolve(classOf[AnyRef])) {

  val log = Logger[BeanConverter]

  override def toJson(instance: Any, aType: ResolvedClass, context: Context): JsonNode = {

    val properties = new mutable.LinkedHashMap[String, JsonNode]
    properties.put("$type", JsonString(instance.getClass.getSimpleName))
    val jsonObject = JsonObject(properties)

    val links = new mutable.LinkedHashMap[String, JsonNode]
    properties.put("$links", JsonObject(links))

    val resolvedType = TypeToken.of(aType.underlying).resolveType(instance.getClass).getType

    val beanModel = BeanIntrospector.createWithType(resolvedType)

    val schema = context.schema

    val propertyMapping = schema.findInstanceMapping(instance.asInstanceOf[AnyRef])

    if (!(aType <:< TypeResolver.resolve(classOf[NodeDescriptor]))) {
      val nodeId = propertyMapping.get("id")

      if (nodeId.isEmpty && beanModel.properties.exists(property => property.name == "id")) {
        log.warn("No Id for: " + aType.raw.getName)
      }
    }

    val ignoreFilter = aType.findDeclaredAnnotation(classOf[IgnoreFilter])

    for (property <- beanModel.properties) {
      val option = propertyMapping.get(property.name)

      if (option.isDefined) {
        val propertySchema = option.get

        if (propertySchema.secured) {
          if (propertySchema.visible) {
            proceed(instance, context, properties, property)
          }
        } else {
          proceed(instance, context, properties, property)
        }
      } else {
        if (ignoreFilter != null) {
          proceed(instance, context, properties, property)
        }
      }

    }

    val registry = context.registry
    
    val linkFactories = schema.findLinks(TypeResolver.rawType(resolvedType))
    
    linkFactories.foreach(linkFactory => {
      generateLinks(linkFactory, instance, context, links, registry)
    })

    if links.isEmpty then properties.remove("$links")

    jsonObject
  }

  private def proceed(instance: Any, context: Context, properties: mutable.LinkedHashMap[String, JsonNode], property: BeanProperty) = {
    val registry = context.registry
    val converter = registry.find(property.propertyType)
    val value = property.get(instance.asInstanceOf[AnyRef])

    value match
      case null =>
      case bool: Boolean => if bool then processProperty(context, properties, property, converter, value)
      case string: String => if string.nonEmpty then processProperty(context, properties, property, converter, value)
      case _ => processProperty(context, properties, property, converter, value)
  }

  private def generateLinks(linkFactory : (Any, LinkContext) => Unit, instance: Any, context: Context, links: mutable.LinkedHashMap[String, JsonNode], registry: ConverterRegistry): Unit = {
    val linksResult = new mutable.HashMap[String, Link]()

    linkFactory(instance, (name: String, link: Link) => linksResult.put(name, link))

    for (link <- linksResult) {
      val converter = registry.find(TypeResolver.resolve(classOf[Link]))
      val node = converter.toJson(link._2, TypeResolver.resolve(classOf[Link]), Context(link._1, context))

      links.put(link._1, node)
    }
  }

  private def processProperty(context: Context, properties: mutable.LinkedHashMap[String, JsonNode], property: BeanProperty, converter: AbstractConverter, value: Any) = value match
    case collection : util.Collection[?] => if ! collection.isEmpty then addProperty(context, properties, property, converter, value)
    case map: util.Map[?, ?] => if !map.isEmpty then addProperty(context, properties, property, converter, value)
    case _ => addProperty(context, properties, property, converter, value)


  private def addProperty(context: Context, properties: mutable.LinkedHashMap[String, JsonNode], property: BeanProperty, converter: AbstractConverter, value: Any) = {
    val jsonNode = converter.toJson(value, property.propertyType, Context(property.name, context))
    properties.put(property.name, jsonNode)
  }

  override def toJava(jsonNode: JsonNode, aType: ResolvedClass, context: Context): Any = jsonNode match
    case jsonObject: JsonObject =>
      val jsonSubTypes = aType.findDeclaredAnnotation(classOf[JsonSubTypes])

      val beanModel = if (jsonSubTypes == null) BeanIntrospector.create(aType) else
        BeanIntrospector.createWithType(
          jsonSubTypes.value().find(subType => subType.value().getSimpleName == jsonObject.value("$type").value).get.value()
        )

      val entity = context.loader.load(jsonObject, beanModel.underlying, Array())

      val schema = context.schema

      val propertyMapping = schema.findTypeMapping(beanModel.underlying.raw)

      if (! (aType <:< TypeResolver.resolve(classOf[NodeDescriptor]))) {
        val nodeId = propertyMapping.get("id")

        if (nodeId.isEmpty && beanModel.properties.exists(property => property.name == "id")) {
          log.warn("No Id for: " + aType.raw.getName)
        }
      }

      val ignoreFilter = aType.findDeclaredAnnotation(classOf[IgnoreFilter])

      for (property <- beanModel.properties) {
        val option = propertyMapping.get(property.name)
        if (option.isDefined || ignoreFilter != null) {
          val descriptor = option.get
          if (descriptor.writeable) {
            
            val registry = context.registry
            val converter = registry.find(property.propertyType)
            val currentNode = jsonObject.value.get(property.name)

            val value = if currentNode.isDefined then 
              converter.toJava(currentNode.get, property.propertyType, Context(property.name, context))
            else
              if classOf[java.lang.Boolean].isAssignableFrom(property.propertyType.raw) then
                false else null

            val violations = context.validator.validateValue(aType.raw, property.name, value)

            if (violations.isEmpty) {
              value match
                case collection: util.Collection[Any] =>
                  val underlyingCollection = property.get(entity).asInstanceOf[util.Collection[Any]]
                  underlyingCollection.clear()
                  underlyingCollection.addAll(collection)
                case map: util.Map[Any, Any] =>
                  val underlyingMap = property.get(entity).asInstanceOf[util.Map[Any, Any]]
                  underlyingMap.clear()
                  underlyingMap.putAll(map)
                case _ => property.set(entity, value)
            } else {
              context.violations.addAll(violations)
            }
          }
        }
      }

      entity
    case _ => throw new IllegalStateException("Not a json Object")

}
