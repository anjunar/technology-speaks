package com.anjunar.technologyspeaks.olama

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class EmbeddingRequest extends AbstractRequest {
  
  @BeanProperty
  var input : String = uninitialized

  @BeanProperty
  var options : RequestOptions = uninitialized

}

object EmbeddingRequest {
  def apply(input : String, options : RequestOptions) : EmbeddingRequest = {
    val embeddingRequest = new EmbeddingRequest
    embeddingRequest.options = options
    embeddingRequest.input = input
    embeddingRequest.model = "gemma3"
    embeddingRequest
  }
}