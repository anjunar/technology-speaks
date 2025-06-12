package com.anjunar.technologyspeaks.semanticspeak

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class EmbeddingResponse {

  var embeddings : Array[Array[Float]] = uninitialized
  
}
