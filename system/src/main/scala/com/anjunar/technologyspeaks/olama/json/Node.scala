package com.anjunar.technologyspeaks.olama.json

import com.fasterxml.jackson.annotation.JsonProperty

trait Node {

  @JsonProperty("type")
  def nodeType: NodeType
  def description: String
  
}
