package com.anjunar.technologyspeaks.olama.json

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class JsonNode {

  @JsonProperty("type")
  @BeanProperty
  var nodeType : NodeType = uninitialized

  @BeanProperty
  var description : String = uninitialized

}
