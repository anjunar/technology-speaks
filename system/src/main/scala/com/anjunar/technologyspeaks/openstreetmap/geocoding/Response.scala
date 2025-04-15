package com.anjunar.technologyspeaks.openstreetmap.geocoding

import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Response {

  @JsonProperty("type")
  @BeanProperty
  var typ : String = uninitialized

  @BeanProperty
  var licence : String = uninitialized
  
  @BeanProperty
  val features : util.List[Feature] = new util.ArrayList[Feature]()

}
