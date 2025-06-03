package com.anjunar.technologyspeaks.olama

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.client.{ClientBuilder, Entity}
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget

import scala.util.control.Breaks.{break, breakable}

@ApplicationScoped
class AsyncOLlamaService {

  def chat(request: ChatRequest, onData: String => Unit): Unit = {
    val client = ClientBuilder.newClient
    try {
      val target = client.target("http://localhost:11434/api/chat")

      val webTarget = target.asInstanceOf[ResteasyWebTarget]
      val resteasyJacksonProvider = new JacksonJsonProvider()
      val mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule)
        .setSerializationInclusion(Include.NON_EMPTY)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

      resteasyJacksonProvider.setMapper(mapper)
      webTarget.register(resteasyJacksonProvider)

      val response = target.request().post(Entity.json(request))
      val inputStream = response.readEntity(classOf[java.io.InputStream])
      val source = scala.io.Source.fromInputStream(inputStream)

      try {
        breakable {
          for (line <- source.getLines()) {
            val chatResponse = mapper.readValue(line, classOf[ChatResponse])
            onData(chatResponse.message.content)
            if (chatResponse.done) break
          }
        }
      } finally {
        source.close()
      }
    } finally {
      client.close()
    }
  }

}
