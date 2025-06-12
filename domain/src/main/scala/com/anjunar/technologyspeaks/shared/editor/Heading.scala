package com.anjunar.technologyspeaks.shared.editor

import jakarta.persistence.Entity

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Heading extends ContainerNode {

  var depth : Int = uninitialized

  override def toString = s"Emphasis($depth, ${super.toString})"

}
