package com.anjunar.technologyspeaks.olama

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class GenerateResponse extends AbstractResponse {

  @BeanProperty
  var response : String = uninitialized

  @BeanProperty
  var context : Array[Int] = uninitialized
  
}
