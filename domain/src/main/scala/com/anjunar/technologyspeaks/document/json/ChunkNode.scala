package com.anjunar.technologyspeaks.document.json

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class ChunkNode {

  @BeanProperty
  var title : String = uninitialized

  @BeanProperty
  var content : String = uninitialized

}
