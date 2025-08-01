package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.JacksonJsonConverter
import com.anjunar.technologyspeaks.configuration.jaxrs.ObjectMapperContextResolver
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import jakarta.persistence.AttributeConverter

class RootConverter extends JacksonJsonConverter {

  val objectMapper = ObjectMapperContextResolver.objectMapper
  
  override def toJava(value: Any): Any = objectMapper.readValue(value.toString, classOf[Root])

  override def toJson(value: Any): String = objectMapper.writeValueAsString(value)

}
