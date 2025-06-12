package com.anjunar.technologyspeaks.olama.json

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class JsonFunctionBody extends JsonNode {

  var name : String = uninitialized

  var parameters : JsonObject = uninitialized

}
