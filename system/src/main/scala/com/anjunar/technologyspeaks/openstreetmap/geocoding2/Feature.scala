package com.anjunar.technologyspeaks.openstreetmap.geocoding2

import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import java.util.{List, Map}
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Feature {

  var id: String = uninitialized
  
  @JsonProperty("place_type")
  @BeanProperty  
  var placeType: util.List[String] = uninitialized

  var relevance: Integer = uninitialized

  var properties: util.Map[String, String] = uninitialized

  var text: String = uninitialized
  
  @JsonProperty("place_name")
  @BeanProperty  
  var placeName: String = uninitialized

  var bbox: util.List[Float] = uninitialized

  var center: util.List[Float] = uninitialized
  
  var geometry: Point = uninitialized
  
  var context: util.List[Context] = uninitialized
}
