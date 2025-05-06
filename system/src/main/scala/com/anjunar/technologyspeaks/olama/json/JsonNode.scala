package com.anjunar.technologyspeaks.olama.json

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class JsonNode {

  @BeanProperty
  var nodeType : NodeType = uninitialized

}
