package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.IgnoreFilter
import jakarta.persistence.Entity

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class TextNode extends AbstractNode {

  @BeanProperty
  var block : String = uninitialized

  @BeanProperty
  var text : String = uninitialized

  @BeanProperty
  var bold : Boolean = false

  @BeanProperty
  var italic : Boolean = false

  @BeanProperty
  var deleted : Boolean = false

  @BeanProperty
  var sub : Boolean = false

  @BeanProperty
  var sup : Boolean = false

  @BeanProperty
  var fontFamily : String = uninitialized

  @BeanProperty
  var fontSize : String = uninitialized

  @BeanProperty
  var color : String = uninitialized

  @BeanProperty
  var backgroundColour : String = uninitialized

}
