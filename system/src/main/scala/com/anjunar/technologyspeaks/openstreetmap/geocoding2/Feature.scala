package com.anjunar.technologyspeaks.openstreetmap.geocoding2

import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import java.util.{List, Map}
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Feature {

  @BeanProperty
  var id: String = uninitialized
  
  @JsonProperty("place_type")
  @BeanProperty  
  var placeType: util.List[String] = uninitialized

  @BeanProperty
  var relevance: Integer = uninitialized

  @BeanProperty
  var properties: util.Map[String, String] = uninitialized

  @BeanProperty
  var text: String = uninitialized
  
  @JsonProperty("place_name")
  @BeanProperty  
  var placeName: String = uninitialized

  @BeanProperty
  var bbox: util.List[Float] = uninitialized

  @BeanProperty
  var center: util.List[Float] = uninitialized
  
  @BeanProperty
  var geometry: Point = uninitialized
  
  @BeanProperty
  var context: util.List[Context] = uninitialized
}
