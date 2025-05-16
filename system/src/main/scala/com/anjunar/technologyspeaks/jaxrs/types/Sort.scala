package com.anjunar.technologyspeaks.jaxrs.types

import com.anjunar.scala.mapper.annotations.Descriptor

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Sort {

  @Descriptor(title = "Property", writeable = true)
  @BeanProperty
  var property : String = uninitialized

  @Descriptor(title = "Value", writeable = true)
  @BeanProperty
  var value : String = uninitialized

}
