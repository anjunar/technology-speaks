package com.anjunar.technologyspeaks.olama.json

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class JsonArray extends JsonNode {

  var items : JsonNode = uninitialized

}

object JsonArray {
  def apply(items : JsonNode) : JsonArray = {
    val array = new JsonArray
    array.nodeType = NodeType.ARRAY
    array.items = items
    array
  }
}