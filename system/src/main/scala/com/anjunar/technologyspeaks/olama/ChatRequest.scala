package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.olama.json.{JsonFunctionBody, JsonFunction, JsonNode, JsonObject}

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

class ChatRequest extends AbstractRequest {

  val messages : util.List[ChatMessage] = new util.ArrayList[ChatMessage]()

  val tools : util.List[JsonFunction] = new util.ArrayList[JsonFunction]()

  var format: JsonNode = uninitialized

  var stream: Boolean = uninitialized
  
  var options : RequestOptions = uninitialized

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