package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.olama.json.{JsonFunctionBody, JsonFunction, JsonNode, JsonObject}

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

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
  var options : RequestOptions = uninitialized

  @BeanProperty
  var keepAlive: String = uninitialized

}

object ChatRequest {
  def apply(format : JsonNode, messages : Seq[ChatMessage], tools : Seq[JsonFunction] = Seq()): ChatRequest = {
    val request = new ChatRequest
    request.model = "gemma3"
    request.options = RequestOptions(0)
    request.messages.addAll(messages.asJava)
    request.tools.addAll(tools.asJava)
    request.format = format
    request.stream = true
    request
  }

  def apply(messages: Seq[ChatMessage]): ChatRequest = {
    val request = new ChatRequest
    request.model = "gemma3"
    request.options = RequestOptions(0)
    request.messages.addAll(messages.asJava)
    request
  }

}