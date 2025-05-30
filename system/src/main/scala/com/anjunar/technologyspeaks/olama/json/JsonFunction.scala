package com.anjunar.technologyspeaks.olama.json

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class JsonFunction extends JsonNode {

  @BeanProperty
  var name : String = uninitialized

  @BeanProperty
  var parameters : JsonObject = uninitialized

}
