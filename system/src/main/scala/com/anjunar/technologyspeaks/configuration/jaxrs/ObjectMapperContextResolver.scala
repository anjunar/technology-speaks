package com.anjunar.technologyspeaks.configuration.jaxrs
import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import jakarta.ws.rs.ext.{ContextResolver, Provider}

@Provider
class ObjectMapperContextResolver extends ContextResolver[ObjectMapper] {

  private val objectMapper: ObjectMapper = new ObjectMapper()
    .registerModule(new Jdk8Module().configureAbsentsAsNulls(true))
    .registerModule(new JavaTimeModule)
    .setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    


  override def getContext(`type`: Class[_]): ObjectMapper = objectMapper
}
