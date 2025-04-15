package com.anjunar.technologyspeaks.openstreetmap.route

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized


class Response {

  @BeanProperty
  var uuid : String = uninitialized
  
  @BeanProperty
  var code : String = uninitialized

  @BeanProperty
  val routes : util.List[Route] = new util.ArrayList[Route]()

  @BeanProperty
  val waypoints: util.List[Waypoint] = new util.ArrayList[Waypoint]()
  
}
