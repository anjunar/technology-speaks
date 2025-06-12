package com.anjunar.technologyspeaks.openstreetmap.route

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Waypoint {

  var distance : Double = uninitialized

  var name: String = uninitialized

  val location: util.List[Double] = new util.ArrayList[Double]()

}
