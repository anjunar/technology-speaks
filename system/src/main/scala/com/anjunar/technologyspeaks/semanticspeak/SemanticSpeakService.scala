package com.anjunar.technologyspeaks.semanticspeak

import com.anjunar.technologyspeaks.configuration.jaxrs.ObjectMapperContextResolver
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.client.{ClientBuilder, Entity}
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget

import scala.util.control.Breaks.{break, breakable}

@ApplicationScoped
class SemanticSpeakService {

  def generateEmbedding(request: TextRequest): EmbeddingResponse = {
    val client = ClientBuilder.newClient
    try {
      val target = client.target("http://localhost:8000/embed")

      val webTarget = target.asInstanceOf[ResteasyWebTarget]
      val resteasyJacksonProvider = new JacksonJsonProvider()
      val mapper = ObjectMapperContextResolver.objectMapper

      resteasyJacksonProvider.setMapper(mapper)
      webTarget.register(resteasyJacksonProvider)

      val response = target.request().post(Entity.json(request))
      response.readEntity(classOf[EmbeddingResponse])

    } finally {
      client.close()
    }
  }

}
