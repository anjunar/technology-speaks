package com.anjunar.technologyspeaks.olama

import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class ChatMessage {

  @BeanProperty
  var role : ChatRole = uninitialized

  @BeanProperty
  var content : String = uninitialized

  @BeanProperty
  @JsonProperty("tool_calls")  
  val toolCalls : util.List[ChatFunction] = new util.ArrayList[ChatFunction]()

  @BeanProperty
  var images: Array[String] = uninitialized
  
}
