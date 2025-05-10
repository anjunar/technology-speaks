package com.anjunar.technologyspeaks.olama

import com.fasterxml.jackson.annotation.JsonProperty

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class ChatResponse extends AbstractResponse {

  @BeanProperty
  var message : ChatMessage = uninitialized

  @JsonProperty("done_reason")
  @BeanProperty
  var doneReason : String = uninitialized

  @BeanProperty
  var done : Boolean = uninitialized

}
