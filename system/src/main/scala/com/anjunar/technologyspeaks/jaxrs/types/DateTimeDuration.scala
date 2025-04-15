package com.anjunar.technologyspeaks.jaxrs.types

import java.time.LocalDateTime
import scala.beans.BeanProperty
import scala.compiletime.uninitialized


class DateTimeDuration {
  
  @BeanProperty
  var from: LocalDateTime = uninitialized
  
  @BeanProperty
  var to: LocalDateTime = uninitialized

}