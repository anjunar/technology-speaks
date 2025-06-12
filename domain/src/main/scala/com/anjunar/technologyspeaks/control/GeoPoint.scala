package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import jakarta.persistence.Embeddable

import scala.beans.BeanProperty
import scala.compiletime.uninitialized


@Embeddable
class GeoPoint {
  
  @PropertyDescriptor(title = "Lan")
  var x : Double = uninitialized

  @PropertyDescriptor(title = "Lat")
  var y : Double = uninitialized
  
  override def toString = s"GeoPoint($x, $y)"
}
