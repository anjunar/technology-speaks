package com.anjunar.technologyspeaks.configuration.jaxrs.converter

import jakarta.ws.rs.ext.ParamConverter

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter


class LocalDateTimeParamConverter extends ParamConverter[LocalDateTime] {
  override def fromString(value: String): LocalDateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

  override def toString(value: LocalDateTime): String = value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}
