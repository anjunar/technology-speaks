package com.anjunar.technologyspeaks.jaxrs.types

import java.time.LocalDate
import scala.beans.BeanProperty
import scala.compiletime.uninitialized


class DateDuration {

  @BeanProperty
  var from: LocalDate = uninitialized
  
  @BeanProperty
  var to: LocalDate = uninitialized

}
