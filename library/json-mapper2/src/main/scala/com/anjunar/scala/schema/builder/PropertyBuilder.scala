package com.anjunar.scala.schema.builder

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.scala.schema.model.Link
import com.anjunar.scala.universe.introspector.{BeanIntrospector, BeanProperty}

import java.time.{Duration, LocalDate, LocalDateTime, LocalTime}
import java.util
import java.util.{Optional, UUID}
import scala.collection.mutable
import scala.compiletime.uninitialized

class PropertyBuilder[C](val name : String, val aClass : Class[?]) {

  val property: BeanProperty = {
    val model = BeanIntrospector.createWithType(aClass)
    model.findProperty(name)
  }

  val annotation: Descriptor = {
    property.findDeclaredAnnotation(classOf[Descriptor])
  }

  var widget : String = {
    if (annotation != null && annotation.widget().nonEmpty) {
      annotation.widget()
    } else {
      property.propertyType.raw match
        case aClass if classOf[util.Collection[?]].isAssignableFrom(aClass) => "lazy-multi-select"
        case aClass if classOf[java.lang.Boolean].isAssignableFrom(aClass) => "checkbox"
        case aClass if classOf[Enum[?]].isAssignableFrom(aClass) => "select"
        case aClass if classOf[Number].isAssignableFrom(aClass) => "number"
        case aClass if classOf[String].isAssignableFrom(aClass) => "text"
        case aClass if classOf[LocalDateTime].isAssignableFrom(aClass) => "datetime-local"
        case aClass if classOf[LocalDate].isAssignableFrom(aClass) => "date"
        case aClass if classOf[LocalTime].isAssignableFrom(aClass) => "time"
        case aClass if classOf[Duration].isAssignableFrom(aClass) => "duration"
        case aClass if classOf[UUID].isAssignableFrom(aClass) => "text"
        case _ => "form"
    }
  }

  var title : String = {
    if (annotation == null) {
      ""
    } else {
      annotation.title()
    }
  }

  var description : String = {
    if (annotation == null) {
      ""
    } else {
      annotation.description()
    }
  }

  var id: Boolean = {
    if (annotation == null) {
      false
    } else {
      annotation.id()
    }
  }

  var naming: Boolean = {
    if (annotation == null) {
      false
    } else {
      annotation.naming()
    }
  }

  var writeable: Boolean = {
    if (annotation == null) {
      false
    } else {
      annotation.writeable()
    }
  }

  var step: String = {
    if (annotation == null) {
      "1"
    } else {
      annotation.step()
    }
  }

  var visible: Boolean = true

  var secured : Boolean = false

  val links = new mutable.LinkedHashMap[String, Link]

  def withLinks(link: LinkContext => Unit): PropertyBuilder[C] = {
    link((name: String, link: Link) => links.put(name, link))
    this
  }

  def withTitle(value : String): PropertyBuilder[C] = {
    title = value
    this
  }

  def withDescription(value : String): PropertyBuilder[C] = {
    description = value
    this
  }

  def withWidget(value : String): PropertyBuilder[C] = {
    widget = value
    this
  }

  def withId(value : Boolean): PropertyBuilder[C] = {
    id = value
    this
  }

  def withNaming(value : Boolean): PropertyBuilder[C] = {
    naming = value
    this
  }

  def withWriteable(value : Boolean): PropertyBuilder[C] = {
    writeable = value
    this
  }
  
  def withStep(value : String) : PropertyBuilder[C] = {
    step = value
    this
  }

  def withManaged(value : String => (Boolean, UUID), link : (UUID, LinkContext) => Unit) : PropertyBuilder[C] = {
    val (isVisible, id) = value(name)

    if (id != null) {
      link(id, (name: String, link: Link) => links.put(name, link))
    }

    visible = isVisible
    secured = true

    this
  }

}
