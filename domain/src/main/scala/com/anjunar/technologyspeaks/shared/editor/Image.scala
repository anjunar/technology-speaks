package com.anjunar.technologyspeaks.shared.editor

import jakarta.persistence.Entity

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Image extends Node {

  @BeanProperty
  var url : String = uninitialized

  @BeanProperty
  var alt : String = uninitialized

}
