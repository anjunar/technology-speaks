package com.anjunar.technologyspeaks.shared.editor

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Marker {

  @BeanProperty
  var column: Int = uninitialized

  @BeanProperty
  var line: Int = uninitialized

  @BeanProperty
  var offset: Int = uninitialized

}
