package com.anjunar.technologyspeaks.shared.editor

import jakarta.persistence.Entity

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class List extends ContainerNode {

  @BeanProperty
  var ordered : Boolean = uninitialized

}
