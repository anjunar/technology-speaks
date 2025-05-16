package com.anjunar.technologyspeaks.configuration.jaxrs
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import jakarta.ws.rs.ext.{ContextResolver, Provider}

@Provider
class ObjectMapperContextResolver extends ContextResolver[ObjectMapper] {

  private val objectMapper: ObjectMapper = new ObjectMapper()
    .registerModule(new Jdk8Module().configureAbsentsAsNulls(true))
    .registerModule(new JavaTimeModule)
    .setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


  override def getContext(`type`: Class[?]): ObjectMapper = objectMapper
}
