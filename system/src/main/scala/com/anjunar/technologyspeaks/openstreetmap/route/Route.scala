package com.anjunar.technologyspeaks.openstreetmap.route

import java.time.Duration
import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Route {

  @BeanProperty
  var weight : Double = uninitialized

  @BeanProperty
  var duration : Double = uninitialized

  @BeanProperty
  var distance : Double = uninitialized

  @BeanProperty
  val legs: util.List[Leg] = new util.ArrayList[Leg]()

  @BeanProperty
  var geometry: Geometry = uninitialized

}
