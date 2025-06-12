package com.anjunar.technologyspeaks.openstreetmap.route

import java.time.Duration
import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Route {

  var weight : Double = uninitialized

  var duration : Double = uninitialized

  var distance : Double = uninitialized

  val legs: util.List[Leg] = new util.ArrayList[Leg]()

  var geometry: Geometry = uninitialized

}
