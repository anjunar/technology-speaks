package com.anjunar.scala.schema.builder

import scala.collection.mutable
import scala.compiletime.uninitialized

class SchemaBuilder(val table : Boolean = false) {
  
  val typeMapping = new mutable.LinkedHashMap[Class[?], EntitySchemaBuilder[?]]

  val instanceMapping = new mutable.LinkedHashMap[Any, EntitySchemaBuilder[?]]

  def forLinks[C](aClass : Class[C], link : (C, LinkContext) => Unit) : SchemaBuilder = {
    val option = typeMapping.get(aClass)

    if (option.isDefined) {
      val value = option.get.asInstanceOf[EntitySchemaBuilder[C]]
      value.withLinks(link)
    } else {
      val value = new EntitySchemaBuilder[C](aClass, table)
      value.withLinks(link)
      typeMapping.put(aClass, value)
    }

    this
  }

  def forLinks[C](instance: C, aClass: Class[C], link : (C, LinkContext) => Unit): SchemaBuilder = {
    val instanceOption = instanceMapping.get(instance)

    if (instanceOption.isDefined) {
      val value = instanceOption.get.asInstanceOf[EntitySchemaBuilder[C]]
      value.withLinks(link)
    } else {
      val value = new EntitySchemaBuilder[C](instance.getClass.asInstanceOf[Class[C]], table)
      value.withLinks(link)
      instanceMapping.put(instance, value)
    }

    this
  }

  def forType[C](aClass : Class[C], builder: EntitySchemaBuilder[C] => Unit) : SchemaBuilder = {

    val option = typeMapping.get(aClass)
    
    if (option.isDefined) {
      val value = option.get.asInstanceOf[EntitySchemaBuilder[C]]
      builder(value)
    } else {
      val value = new EntitySchemaBuilder[C](aClass, table)
      builder(value)
      typeMapping.put(aClass, value)
    }
    
    this
  }

  def forInstance[C](instance : C, aClass : Class[C], builder: EntitySchemaBuilder[C] => Unit) : SchemaBuilder = {
    if (instance == null) {
      forType(aClass, builder)
    } else {
      val instanceOption = instanceMapping.get(instance)

      if (instanceOption.isDefined) {
        val value = instanceOption.get.asInstanceOf[EntitySchemaBuilder[C]]
        builder(value)
      } else {
        val value = new EntitySchemaBuilder[C](instance.getClass.asInstanceOf[Class[C]], table)
        builder(value)
        instanceMapping.put(instance, value)
      }
    }
    this
  }

  def findTypeMapping(aClass : Class[?]) : Map[String, PropertyBuilder[?]] = {
    typeMapping
      .filter(entry => entry._1.isAssignableFrom(aClass))
      .flatMap(entry => entry._2.mapping)
      .toMap
  }

  def findInstanceMapping(instance : AnyRef): Map[String, PropertyBuilder[?]] = {
    val propertyMapping = instanceMapping
      .filter(entry => entry._1 == instance)
      .flatMap(entry => entry._2.mapping)
      .toMap
    
    if (propertyMapping.isEmpty) {
      findTypeMapping(instance.getClass)
    } else {
      propertyMapping
    }
  }

  def findEntitySchemaDeepByClass(aClass: Class[?]): Iterable[EntitySchemaBuilder[?]] = {
    typeMapping.values ++ typeMapping
      .values
      .flatMap(builder => builder.mapping)
      .flatMap(properties => properties._2.schemaBuilder.findEntitySchemaDeepByClass(aClass))
      .filter(builder => builder.aClass == aClass)
  }

  def findEntitySchemaDeepByInstance(instance : Any): Iterable[EntitySchemaBuilder[?]] = {
    val builders: Iterable[SchemaBuilder] = findSchemaBuilderDeep

    builders
      .flatMap(schema => schema.instanceMapping
      .filter((aClass, builder) => aClass == instance)
        .map((aClass, builder) => builder)
      )
  }

  private def findSchemaBuilderDeep: Iterable[SchemaBuilder] = {
    Seq(this) ++ typeMapping
      .values
      .flatMap(builder => builder.mapping.values.flatMap(property => property.schemaBuilder.findSchemaBuilderDeep))
  }

  def findLinksByClass(aClass: Class[?]): mutable.Iterable[(Any, LinkContext) => Unit] = {
    typeMapping
      .filter(entry => entry._1.isAssignableFrom(aClass) && entry._2.links != null)
      .map(entry => entry._2.links)
      .asInstanceOf[mutable.Iterable[(Any, LinkContext) => Unit]]
  }

  def findLinksByInstance(instance : Any): mutable.Iterable[(Any, LinkContext) => Unit] = {
    instanceMapping
      .filter((i, builder) => i == instance)
      .map(entry => entry._2.links)
      .asInstanceOf[mutable.Iterable[(Any, LinkContext) => Unit]]
  }


}
