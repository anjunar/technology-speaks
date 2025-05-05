package com.anjunar.scala.schema.builder

import com.anjunar.scala.schema.model.Link

import scala.collection.mutable
import scala.compiletime.uninitialized

class EntitySchemaBuilder[C](val aClass : Class[C], isTable : Boolean, parent : SchemaBuilder) {
  
  val mapping = new mutable.LinkedHashMap[String, PropertyBuilder[C]]
  
  var links : (instance : AnyRef, link: LinkContext) => Unit = uninitialized
  
  def property(name : String, builder: PropertyBuilder[C] => PropertyBuilder[C]) : EntitySchemaBuilder[C] = {
    val propertyBuilder = new PropertyBuilder[C](name, aClass, isTable, parent)
    mapping.put(name, propertyBuilder)
    builder.apply(propertyBuilder)
    this
  }

  def property(name : String) : EntitySchemaBuilder[C] = {
    val property = new PropertyBuilder[C](name, aClass, isTable, parent)
    mapping.put(name, property)
    this
  }
  
  def withLinks(link : (C, LinkContext) => Unit): EntitySchemaBuilder[C] = {
    links = link.asInstanceOf[(AnyRef, LinkContext) => Unit]
    this
  }
  
}
