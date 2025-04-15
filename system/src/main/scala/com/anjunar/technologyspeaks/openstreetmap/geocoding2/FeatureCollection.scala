package com.anjunar.technologyspeaks.openstreetmap.geocoding2

import java.util
import java.util.List
import scala.beans.BeanProperty

class FeatureCollection {

  @BeanProperty
  val query: util.List[String] = new util.ArrayList[String]()

  @BeanProperty
  val features: util.List[Feature] = new util.ArrayList[Feature]()

}
