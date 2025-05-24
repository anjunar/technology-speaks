package com.anjunar.technologyspeaks.olama.json

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class JsonFunctionWrapper extends JsonNode {

  @BeanProperty
  var function : JsonFunction = uninitialized

}
