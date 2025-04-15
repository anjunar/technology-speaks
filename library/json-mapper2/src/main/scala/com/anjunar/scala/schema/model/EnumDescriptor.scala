package com.anjunar.scala.schema.model

import com.anjunar.scala.mapper.annotations.IgnoreFilter

import java.util
import scala.beans.BeanProperty

@IgnoreFilter
class EnumDescriptor extends NodeDescriptor {
  
  @BeanProperty
  var enums : util.List[String] = new util.ArrayList[String]()
  
}

object EnumDescriptor {
  def apply(title: String, 
            description: String,
            widget: String, 
            id: Boolean, 
            name: Boolean, 
            writeable: Boolean, 
            aType: String, 
            links: util.Map[String, Link],
            enums : util.List[String]): EnumDescriptor = {
    
    val descriptor = new EnumDescriptor
    descriptor.title = title
    descriptor.description = description
    descriptor.widget = widget
    descriptor.id = id
    descriptor.name = name
    descriptor.writeable = writeable
    descriptor.`type` = aType
    descriptor.links = links
    descriptor.enums = enums
    descriptor
  }
}
