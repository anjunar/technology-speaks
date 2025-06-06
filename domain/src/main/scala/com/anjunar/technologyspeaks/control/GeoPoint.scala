package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.Descriptor
import jakarta.persistence.Embeddable

import scala.beans.BeanProperty
import scala.compiletime.uninitialized


@Embeddable
class GeoPoint {
  
  @BeanProperty
  @Descriptor(title = "Lan")
  var x : Double = uninitialized

  @BeanProperty
  @Descriptor(title = "Lat")
  var y : Double = uninitialized
  
  override def toString = s"GeoPoint($x, $y)"
}
