package com.anjunar.technologyspeaks.openstreetmap.geocoding2

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Context {

  var id: String = uninitialized

  @JsonProperty("short_code")
  var shortCode : String = uninitialized

  var wikidata: String = uninitialized
  
  var text: String = uninitialized
}
