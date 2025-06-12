package com.anjunar.technologyspeaks.olama

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class EmbeddingRequest extends AbstractRequest {
  
  var input : String = uninitialized

  var options : RequestOptions = uninitialized

}

object EmbeddingRequest {
  def apply(input : String, options : RequestOptions) : EmbeddingRequest = {
    val embeddingRequest = new EmbeddingRequest
    embeddingRequest.options = options
    embeddingRequest.input = input
    embeddingRequest.model = "nomic-embed-text"
    embeddingRequest
  }
}