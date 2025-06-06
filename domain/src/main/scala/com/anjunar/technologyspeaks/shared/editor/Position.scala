package com.anjunar.technologyspeaks.shared.editor

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Position {

  @BeanProperty
  var start : Marker = uninitialized

  @BeanProperty
  var end : Marker = uninitialized
  
  override def toString = s"Position($start, $end)"
}
