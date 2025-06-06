package com.anjunar.technologyspeaks.shared.editor

import jakarta.persistence.Entity

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Table extends ContainerNode {

  @BeanProperty
  var align : String = uninitialized

  override def toString = s"Table($align, ${super.toString})"
}
