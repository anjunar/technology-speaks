package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.olama.json.JsonObject

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class GenerateRequest extends AbstractRequest {

  var prompt : String = uninitialized

  var suffix : String = uninitialized

  var images : Array[String] = uninitialized

  var format : JsonObject = uninitialized

  var template : String = uninitialized

  var stream : Boolean = uninitialized

  var keepAlive : String = uninitialized

}
