package com.anjunar.technologyspeaks.openstreetmap.geocoding

import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Feature {

  @JsonProperty("type")
  @BeanProperty
  var typ : String = uninitialized
  
  @BeanProperty
  val properties: util.Map[String, Any] = new util.HashMap()

  @BeanProperty
  val bbox: util.List[Double] = new util.ArrayList()

  @BeanProperty
  var geometry: Point = uninitialized


}
