package com.anjunar.technologyspeaks.openstreetmap.geocoding

import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Point {

  @JsonProperty("type")
  @BeanProperty  
  var typ : String = uninitialized

  @BeanProperty
  val coordinates : util.List[Double] = new util.ArrayList()

}
