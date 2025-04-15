package com.anjunar.technologyspeaks.configuration.jaxrs.converter

import com.anjunar.technologyspeaks.jaxrs.types.DateTimeDuration
import jakarta.ws.rs.ext.ParamConverter

import java.time.LocalDateTime
import java.util.regex.{Matcher, Pattern}


class DateTimeDurationConverter extends ParamConverter[DateTimeDuration] {
  private[jaxrs] val pattern = Pattern.compile("from(.*)to(.*)")

  override def fromString(value: String): DateTimeDuration = {
    val matcher = pattern.matcher(value)
    if (matcher.matches) {
      val duration = new DateTimeDuration
      val from = matcher.group(1)
      val to = matcher.group(2)
      duration.from = LocalDateTime.parse(from)
      duration.to = LocalDateTime.parse(to)
      return duration
    }
    null
  }

  override def toString(value: DateTimeDuration): String = "from" + value.from.toString + "to" + value.to.toString
}
