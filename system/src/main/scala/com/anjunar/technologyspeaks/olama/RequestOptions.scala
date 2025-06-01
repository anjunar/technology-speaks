package com.anjunar.technologyspeaks.olama

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class RequestOptions {
  
  @BeanProperty
  var temperature : Int = uninitialized

}

object RequestOptions {
  def apply(temperature: Int) : RequestOptions = {
    val options = new RequestOptions
    options.temperature = temperature
    options 
  }
}
