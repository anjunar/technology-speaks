package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.olama.ChatFunction.Callee

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class ChatFunction {

  @BeanProperty
  var function : Callee = uninitialized 

}

object ChatFunction {

  class Callee {

    @BeanProperty
    var name : String = uninitialized

    @BeanProperty
    val arguments : util.LinkedHashMap[String, Any] = new util.LinkedHashMap[String, Any]()

  }

}
