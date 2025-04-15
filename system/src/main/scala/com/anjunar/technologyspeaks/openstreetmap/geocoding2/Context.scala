package com.anjunar.technologyspeaks.openstreetmap.geocoding2

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Context {

  @BeanProperty
  var id: String = uninitialized

  @BeanProperty
  @JsonProperty("short_code") 
  var shortCode : String = uninitialized

  @BeanProperty
  var wikidata: String = uninitialized
  
  @BeanProperty
  var text: String = uninitialized
}
