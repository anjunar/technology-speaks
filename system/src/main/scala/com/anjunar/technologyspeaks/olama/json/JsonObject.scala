package com.anjunar.technologyspeaks.olama.json

import java.util
import scala.beans.BeanProperty

class JsonObject extends JsonNode {
  
  @BeanProperty
  val properties : util.LinkedHashMap[String, JsonNode] = new util.LinkedHashMap[String, JsonNode]()
  
  @BeanProperty
  val required : util.Set[String] = new util.HashSet[String]()
  
}
