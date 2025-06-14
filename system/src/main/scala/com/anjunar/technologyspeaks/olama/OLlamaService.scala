package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.configuration.jaxrs.ObjectMapperContextResolver
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider
import jakarta.annotation.{PostConstruct, PreDestroy}
import jakarta.enterprise.context.{ApplicationScoped, RequestScoped, SessionScoped}
import jakarta.ws.rs.client.{Client, ClientBuilder, Entity}
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget

import java.io.InputStream
import scala.compiletime.uninitialized

@ApplicationScoped
class OLlamaService extends Serializable {

  private var client: Client = uninitialized
  private var webTarget: ResteasyWebTarget = uninitialized

  @PostConstruct
  def setup(): Unit = {
    client = ClientBuilder.newClient()
    client.register(classOf[LoggingRequestFilter])

    val mapper = ObjectMapperContextResolver.objectMapper

    val target = client.target("http://localhost:11434")
      .asInstanceOf[ResteasyWebTarget]

    webTarget = target
  }

  @PreDestroy
  def shutdown(): Unit = {
    if (client != null) client.close()
  }

  private def proxy: OLlamaResource = webTarget.proxy(classOf[OLlamaResource])

  def generate(request: GenerateRequest): GenerateResponse = {
    val client = ClientBuilder.newClient
    try {
      val target = client.target("http://localhost:11434/api/generate")

      val webTarget = target.asInstanceOf[ResteasyWebTarget]
      val resteasyJacksonProvider = new JacksonJsonProvider()
      val mapper = ObjectMapperContextResolver.objectMapper

      resteasyJacksonProvider.setMapper(mapper)
      webTarget.register(resteasyJacksonProvider)

      val response = target.request().post(Entity.json(request))
      val body = response.readEntity(classOf[InputStream])

      mapper.readValue(body, classOf[GenerateResponse])

    } finally {
      client.close()
    }
  }

  def chat(request: ChatRequest): ChatResponse = {
    val client = ClientBuilder.newClient
    try {
      val target = client.target("http://localhost:11434/api/chat")

      val webTarget = target.asInstanceOf[ResteasyWebTarget]
      val resteasyJacksonProvider = new JacksonJsonProvider()
      val mapper = ObjectMapperContextResolver.objectMapper

      resteasyJacksonProvider.setMapper(mapper)
      webTarget.register(resteasyJacksonProvider)

      val response = target.request().post(Entity.json(request))
      val body = response.readEntity(classOf[InputStream])

      mapper.readValue(body, classOf[ChatResponse])

    } finally {
      client.close()
    }
  }

  def generateEmbeddings(request: EmbeddingRequest): EmbeddingResponse =
    proxy.generateEmbeddings(request)
}
