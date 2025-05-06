package com.anjunar.technologyspeaks.olama

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class EmbeddingRequest extends AbstractRequest {
  
  @BeanProperty
  var input : String = uninitialized

}
