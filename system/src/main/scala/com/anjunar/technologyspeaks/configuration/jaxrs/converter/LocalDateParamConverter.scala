package com.anjunar.technologyspeaks.configuration.jaxrs.converter

import jakarta.ws.rs.ext.ParamConverter

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class LocalDateParamConverter extends ParamConverter[LocalDate] {
  override def fromString(value: String): LocalDate = LocalDate.parse(value, DateTimeFormatter.ISO_DATE)

  override def toString(value: LocalDate): String = value.format(DateTimeFormatter.ISO_DATE)
}
