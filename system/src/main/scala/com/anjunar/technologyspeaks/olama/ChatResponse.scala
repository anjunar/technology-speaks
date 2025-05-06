package com.anjunar.technologyspeaks.olama

import java.util
import scala.beans.BeanProperty

class ChatResponse extends AbstractResponse {

  @BeanProperty
  val message : util.List[ChatMessage] = new util.ArrayList[ChatMessage]()

}
