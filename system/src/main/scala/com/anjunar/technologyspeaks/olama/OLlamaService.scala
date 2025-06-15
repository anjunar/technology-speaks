package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.configuration.jaxrs.ObjectMapperContextResolver
import com.anjunar.technologyspeaks.jaxrs.JacksonJsonProvider
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl

import scala.compiletime.uninitialized

@ApplicationScoped
class OLlamaService extends Serializable {

  private var webTarget: ResteasyWebTarget = uninitialized

  @PostConstruct
  def setup(): Unit = {
    val mapper = ObjectMapperContextResolver.objectMapper
    val resteasyJacksonProvider = new JacksonJsonProvider()
    resteasyJacksonProvider.setMapper(mapper)

    val client = new ResteasyClientBuilderImpl().build()
    client.register(classOf[LoggingRequestFilter])
    client.register(resteasyJacksonProvider)

    val target = client.target("http://localhost:11434")

    webTarget = target
  }

  private def proxy: OLlamaResource = webTarget.proxy(classOf[OLlamaResource])

  def generate(request: GenerateRequest): String = proxy.generate(request).response

  def chat(request: ChatRequest): String = proxy.chat(request).message.content

  def generateEmbeddings(request: EmbeddingRequest): Array[Float] = proxy.generateEmbeddings(request).embeddings.head
}
