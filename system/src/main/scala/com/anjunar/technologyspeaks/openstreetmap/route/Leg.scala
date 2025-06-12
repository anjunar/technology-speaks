package com.anjunar.technologyspeaks.openstreetmap.route

import com.fasterxml.jackson.annotation.JsonProperty

import java.time.Duration
import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Leg {

  @JsonProperty("via_waypoints")
  val viaWaypoints: util.List[Any] = new util.ArrayList[Any]()

  var weight : Double = uninitialized

  var duration : Double = uninitialized

  val steps: util.List[Any] = new util.ArrayList[Any]()

  var distance : Double = uninitialized

  var summary: String = uninitialized

}
