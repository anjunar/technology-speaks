package com.anjunar.technologyspeaks.olama

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class AbstractRequest {

  @BeanProperty
  var model: String = uninitialized


}

