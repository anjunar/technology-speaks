package com.anjunar.technologyspeaks.olama

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider
import jakarta.annotation.PreDestroy
import jakarta.enterprise.context.{RequestScoped, SessionScoped}
import jakarta.ws.rs.client.ClientBuilder
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget

@SessionScoped
class OLlamaService extends Serializable {

  private val client = ClientBuilder.newClient
  client.register(classOf[LoggingRequestFilter])

  private val target = client.target("http://localhost:11434")

  private val webTarget = target.asInstanceOf[ResteasyWebTarget]
  private val resteasyJacksonProvider = new JacksonJsonProvider()
  private val mapper = new ObjectMapper()
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  resteasyJacksonProvider.setMapper(mapper)
  webTarget.register(resteasyJacksonProvider)

  private val service = webTarget.proxy(classOf[OLlamaResource])

  @PreDestroy
  def postDestroy() : Unit = {
    client.close()
  }
  
  def generate(request: GenerateRequest): GenerateResponse = service.generate(request)

  def chat(request: ChatRequest): ChatResponse = service.chat(request)

  def generateEmbeddings(request: EmbeddingRequest): EmbeddingResponse = service.generateEmbeddings(request)
  
  def close() : Unit = webTarget.getResteasyClient.close()

}
