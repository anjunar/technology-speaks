package com.anjunar.technologyspeaks.openstreetmap.route

import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Geometry {

  @BeanProperty
  val coordinates: util.List[util.List[Double]] = new util.ArrayList[util.List[Double]]()

  @BeanProperty
  @JsonProperty("type")
  var typ: String = uninitialized

}