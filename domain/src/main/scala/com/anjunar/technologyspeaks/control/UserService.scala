package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.olama.{EmbeddingRequest, OLlamaService}
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager

import scala.compiletime.uninitialized

@ApplicationScoped
class UserService {

  @Inject
  var service: OLlamaService = uninitialized

  @Inject
  var entityManager: EntityManager = uninitialized

  def createEmbeddings(text: String): Array[Float] = {
    val request = new EmbeddingRequest
    request.input = text
    request.model = "Llama3.2"

    service.generateEmbeddings(request).embeddings.head
  }

  def update(user: User): Unit = {

    val nickNameVector = createEmbeddings(user.nickName)

    val fullNameVector = if (user.info != null) {
      createEmbeddings(s"${user.nickName}, ${user.info.firstName}, ${user.info.lastName}")
    } else {
      null
    }

    user.nickNameVector = nickNameVector
    user.fullNameVector = fullNameVector
  }

}
