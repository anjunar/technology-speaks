package com.anjunar.technologyspeaks.olama.json

import java.util
import scala.beans.BeanProperty
import scala.jdk.CollectionConverters.*

class JsonObject extends JsonNode {
  
  @BeanProperty
  val properties : util.LinkedHashMap[String, JsonNode] = new util.LinkedHashMap[String, JsonNode]()
  
  @BeanProperty
  val required : util.Set[String] = new util.HashSet[String]()
  
}

object JsonObject {
  def apply(tuples : (String, JsonNode)*) : JsonObject = {
    val json = new JsonObject
    json.nodeType = NodeType.OBJECT
    json.properties.putAll(tuples.toMap.asJava)
    json
  }
}