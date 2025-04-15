package com.anjunar.technologyspeaks.openstreetmap.route

import com.fasterxml.jackson.annotation.JsonProperty

import java.time.Duration
import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Leg {

  @BeanProperty
  @JsonProperty("via_waypoints")
  val viaWaypoints: util.List[Any] = new util.ArrayList[Any]()

  @BeanProperty
  var weight : Double = uninitialized

  @BeanProperty
  var duration : Double = uninitialized

  @BeanProperty
  val steps: util.List[Any] = new util.ArrayList[Any]()

  @BeanProperty
  var distance : Double = uninitialized

  @BeanProperty
  var summary: String = uninitialized

}
