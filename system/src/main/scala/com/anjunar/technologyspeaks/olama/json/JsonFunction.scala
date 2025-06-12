package com.anjunar.technologyspeaks.olama.json

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class JsonFunction extends JsonNode {

  var function : JsonFunctionBody = uninitialized

}

object JsonFunction {
  
  def apply(name : String, parameters : JsonObject, description : String = null) : JsonFunction = {
    val body = new JsonFunctionBody
    body.name = name
    body.parameters = parameters
    body.description = description
    
    val jsonFunction = new JsonFunction
    jsonFunction.function = body
    jsonFunction.nodeType = NodeType.FUNCTION
    jsonFunction
  }
  
}
