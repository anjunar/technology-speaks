package com.anjunar.technologyspeaks.olama.json

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class JsonNode {

  @JsonProperty("type")
  var nodeType : NodeType = uninitialized

  var description : String = uninitialized

}

object JsonNode {
  
  def apply(nodeType : NodeType, description : String = null) : JsonNode = {
    val node = new JsonNode
    node.nodeType = nodeType
    node.description = description
    node
  }
  
}