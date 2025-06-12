package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.olama.json.JsonFunction
import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

class ChatMessage {

  var role : ChatRole = uninitialized

  var content : String = uninitialized

  @JsonProperty("tool_calls")
  val toolCalls : util.List[ChatFunction] = new util.ArrayList[ChatFunction]()

  var images: Array[String] = uninitialized
  
}

object ChatMessage {
  def apply(text : String, role : ChatRole) : ChatMessage = {
    val message = new ChatMessage
    message.content = text
    message.role = role
    message
  }

  def apply(text : String) : ChatMessage = {
    val message = new ChatMessage
    message.content = text
    message.role = ChatRole.USER
    message
  }

  def apply(toolCalls : Seq[ChatFunction]): ChatMessage = {
    val message = new ChatMessage
    message.toolCalls.addAll(toolCalls.asJava)
    message.role = ChatRole.ASSISTANT
    message
  }

}
