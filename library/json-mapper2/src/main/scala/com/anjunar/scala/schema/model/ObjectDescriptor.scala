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

object ObjectDescriptor {
  def apply(title: String, description: String, widget: String, id: Boolean, name: Boolean, writeable: Boolean, aType: String, links: util.Map[String, Link]): NodeDescriptor = {
    val descriptor = new NodeDescriptor
    descriptor.title = title
    descriptor.description = description
    descriptor.widget = widget
    descriptor.id = id
    descriptor.name = name
    descriptor.writeable = writeable
    descriptor.`type` = aType
    descriptor.links = links
    descriptor
  }
}
