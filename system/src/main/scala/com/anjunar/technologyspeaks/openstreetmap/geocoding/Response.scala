package com.anjunar.technologyspeaks.openstreetmap.geocoding

import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Response {

  @JsonProperty("type")
  var typ : String = uninitialized

  var licence : String = uninitialized
  
  val features : util.List[Feature] = new util.ArrayList[Feature]()

}
