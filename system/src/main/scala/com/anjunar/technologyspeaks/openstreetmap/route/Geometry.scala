package com.anjunar.technologyspeaks.openstreetmap.route

import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Geometry {

  val coordinates: util.List[util.List[Double]] = new util.ArrayList[util.List[Double]]()

  @JsonProperty("type")
  var typ: String = uninitialized

}