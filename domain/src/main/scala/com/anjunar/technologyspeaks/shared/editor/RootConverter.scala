package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.JacksonJsonConverter
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import jakarta.persistence.AttributeConverter

class RootConverter extends JacksonJsonConverter {

  val objectMapper = new ObjectMapper()
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


  override def toJava(value: Any): Any = objectMapper.readValue(value.toString, classOf[Root])

  override def toJson(value: Any): String = objectMapper.writeValueAsString(value)

}
