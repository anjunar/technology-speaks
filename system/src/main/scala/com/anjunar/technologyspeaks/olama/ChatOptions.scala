package com.anjunar.technologyspeaks.olama

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class ChatOptions {
  
  @BeanProperty
  var temperature : Int = uninitialized

}
