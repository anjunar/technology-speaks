package com.anjunar.scala.schema.model

import com.anjunar.scala.mapper.annotations.IgnoreFilter
import com.anjunar.scala.schema.JsonDescriptorsGenerator
import com.anjunar.scala.schema.model.validators.Validator
import com.anjunar.scala.universe.TypeResolver

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@IgnoreFilter
class NodeDescriptor {

  @BeanProperty
  var title: String = ""

  @BeanProperty
  var description: String = ""

  @BeanProperty
  var widget: String = ""

  @BeanProperty
  var id: Boolean = false

  @BeanProperty
  var name: Boolean = false

  @BeanProperty
  var writeable: Boolean = false

  @BeanProperty
  var hidden : Boolean = false

  @BeanProperty
  var `type`: String = ""

  @BeanProperty
  var step : String = uninitialized

  @BeanProperty
  var links: util.Map[String, Link] = new util.LinkedHashMap[String, Link]()

  @BeanProperty
  var validators : util.Map[String, Validator] = new util.HashMap[String, Validator]()

}

object NodeDescriptor {
  def apply(title: String, description: String, widget: String, id: Boolean, name: Boolean, writeable: Boolean, hidden : Boolean, aType : String, step : String, links: util.Map[String, Link]): NodeDescriptor = {
    val descriptor = new NodeDescriptor
    descriptor.title = title
    descriptor.description = description
    descriptor.widget = widget
    descriptor.id = id
    descriptor.name = name
    descriptor.writeable = writeable
    descriptor.hidden = hidden
    descriptor.`type` = aType
    descriptor.step = step
    descriptor.links = links
    descriptor
  }
}
