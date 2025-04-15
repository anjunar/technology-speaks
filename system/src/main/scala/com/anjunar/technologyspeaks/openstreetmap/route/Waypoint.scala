package com.anjunar.technologyspeaks.openstreetmap.route

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Waypoint {

  @BeanProperty
  var distance : Double = uninitialized

  @BeanProperty
  var name: String = uninitialized

  @BeanProperty
  val location: util.List[Double] = new util.ArrayList[Double]()

}
