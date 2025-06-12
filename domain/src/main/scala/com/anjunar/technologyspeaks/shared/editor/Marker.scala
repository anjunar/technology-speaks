package com.anjunar.technologyspeaks.shared.editor

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Marker {

  var column: Int = uninitialized

  var line: Int = uninitialized

  var offset: Int = uninitialized
  
  override def toString = s"Marker($column, $line, $offset)"
}
