package com.anjunar.technologyspeaks.configuration.jaxrs.converter

import com.anjunar.technologyspeaks.jaxrs.types.DateDuration
import jakarta.ws.rs.ext.ParamConverter

import java.time.LocalDate
import java.util.regex.{Matcher, Pattern}


class DateDurationConverter extends ParamConverter[DateDuration] {
  private[jaxrs] val pattern = Pattern.compile("from(.*)to(.*)")

  override def fromString(value: String): DateDuration = {
    val matcher = pattern.matcher(value)
    if (matcher.matches) {
      val duration = new DateDuration
      val from = matcher.group(1)
      val to = matcher.group(2)
      duration.from = if from != "" then LocalDate.parse(from) else null
      duration.to = if to != "" then LocalDate.parse(to) else null
      return duration
    }
    null
  }

  override def toString(value: DateDuration): String = "from" + value.from.toString + "to" + value.to.toString
}
