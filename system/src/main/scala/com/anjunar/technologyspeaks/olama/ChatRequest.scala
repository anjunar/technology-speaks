package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.olama.json.{JsonFunction, JsonFunctionWrapper, JsonNode, JsonObject}

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

class ChatRequest extends AbstractRequest {

  @BeanProperty
  val messages : util.List[ChatMessage] = new util.ArrayList[ChatMessage]()

  @BeanProperty
  val tools : util.List[JsonFunctionWrapper] = new util.ArrayList[JsonFunctionWrapper]()

  @BeanProperty
  var format: JsonNode = uninitialized

  @BeanProperty
  var stream: Boolean = uninitialized
  
  @BeanProperty
  var options : RequestOptions = uninitialized

  @BeanProperty
  var keepAlive: String = uninitialized

}

object ChatRequest {
  def apply(format : JsonNode, options : RequestOptions, messages : ChatMessage*): ChatRequest = {
    val request = new ChatRequest
    request.model = "gemma3"
    request.options = options
    request.messages.addAll(messages.asJava)
    request.format = format
    request
  }
}