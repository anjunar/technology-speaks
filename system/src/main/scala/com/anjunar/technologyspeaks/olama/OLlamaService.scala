package com.anjunar.technologyspeaks.olama

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider
import jakarta.annotation.{PostConstruct, PreDestroy}
import jakarta.enterprise.context.{ApplicationScoped, RequestScoped, SessionScoped}
import jakarta.ws.rs.client.{Client, ClientBuilder}
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget

import scala.compiletime.uninitialized

@ApplicationScoped
class OLlamaService extends Serializable {

  private var client: Client = uninitialized
  private var webTarget: ResteasyWebTarget = uninitialized

  @PostConstruct
  def setup(): Unit = {
    client = ClientBuilder.newClient()
    client.register(classOf[LoggingRequestFilter])

    val mapper = new ObjectMapper()
      .registerModule(new JavaTimeModule)
      .setSerializationInclusion(Include.NON_EMPTY)
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val jacksonProvider = new JacksonJsonProvider()
    jacksonProvider.setMapper(mapper)

    client.register(jacksonProvider)

    val target = client.target("http://localhost:11434")
      .asInstanceOf[ResteasyWebTarget]

    webTarget = target
  }

  @PreDestroy
  def shutdown(): Unit = {
    if (client != null) client.close()
  }

  private def proxy: OLlamaResource = webTarget.proxy(classOf[OLlamaResource])

  def generate(request: GenerateRequest): GenerateResponse =
    proxy.generate(request)

  def chat(request: ChatRequest): ChatResponse =
    proxy.chat(request)

  def generateEmbeddings(request: EmbeddingRequest): EmbeddingResponse =
    proxy.generateEmbeddings(request)
}
