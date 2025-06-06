package com.anjunar.technologyspeaks.security

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Confirmation {

  @BeanProperty
  var code: String = uninitialized
  
  override def toString = s"Confirmation($code)"
}
