package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.olama.json.{JsonFunction, JsonNode, JsonObject}

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class ChatRequest extends AbstractRequest {

  @BeanProperty
  val messages : util.List[ChatMessage] = new util.ArrayList[ChatMessage]()

  @BeanProperty
  val tools : util.List[JsonFunction] = new util.ArrayList[JsonFunction]()

  @BeanProperty
  var format: JsonNode = uninitialized

  @BeanProperty
  var stream: Boolean = uninitialized

  @BeanProperty
  var keepAlive: String = uninitialized

}
