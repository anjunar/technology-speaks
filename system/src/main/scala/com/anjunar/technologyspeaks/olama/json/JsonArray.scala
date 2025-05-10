package com.anjunar.technologyspeaks.olama.json

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class JsonArray extends JsonNode {

  @BeanProperty
  var items : JsonNode = uninitialized

}
