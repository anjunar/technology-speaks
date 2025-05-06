package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.olama.json.JsonObject

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class GenerateRequest extends AbstractRequest {

  @BeanProperty
  var prompt : String = uninitialized

  @BeanProperty
  var suffix : String = uninitialized

  @BeanProperty
  var images : Array[String] = uninitialized

  @BeanProperty
  var format : JsonObject = uninitialized

  @BeanProperty
  var template : String = uninitialized

  @BeanProperty
  var stream : Boolean = uninitialized

  @BeanProperty
  var keepAlive : String = uninitialized

}
