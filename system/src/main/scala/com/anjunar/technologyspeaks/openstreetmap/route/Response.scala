package com.anjunar.technologyspeaks.openstreetmap.route

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized


class Response {

  var uuid : String = uninitialized
  
  var code : String = uninitialized

  val routes : util.List[Route] = new util.ArrayList[Route]()

  val waypoints: util.List[Waypoint] = new util.ArrayList[Waypoint]()
  
}
