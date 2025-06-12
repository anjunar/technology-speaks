package com.anjunar.technologyspeaks.openstreetmap.geocoding

import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Feature {

  @JsonProperty("type")
  var typ : String = uninitialized
  
  val properties: util.Map[String, Any] = new util.HashMap()

  val bbox: util.List[Double] = new util.ArrayList()

  var geometry: Point = uninitialized


}
