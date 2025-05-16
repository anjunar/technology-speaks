package com.anjunar.scala.schema.model

import com.anjunar.scala.mapper.annotations.IgnoreFilter

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@IgnoreFilter
class ObjectDescriptor extends NodeDescriptor {

  @BeanProperty
  val properties : util.Map[String, NodeDescriptor] = new util.LinkedHashMap[String, NodeDescriptor]()

  @BeanProperty
  val oneOf : util.List[ObjectDescriptor] = new util.ArrayList[ObjectDescriptor]()
  
}