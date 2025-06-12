package com.anjunar.technologyspeaks.shared.editor

import jakarta.persistence.Entity

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class List extends ContainerNode {

  var ordered : Boolean = uninitialized
  
  override def toString = s"List($ordered, ${super.toString})"
}
