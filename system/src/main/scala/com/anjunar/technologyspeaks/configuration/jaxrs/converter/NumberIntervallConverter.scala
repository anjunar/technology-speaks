package com.anjunar.technologyspeaks.configuration.jaxrs.converter

import com.anjunar.technologyspeaks.jaxrs.types.LongIntervall
import jakarta.ws.rs.ext.ParamConverter

import java.util.regex.{Matcher, Pattern}


class NumberIntervallConverter extends ParamConverter[LongIntervall] {
  
  private[jaxrs] val pattern = Pattern.compile("from(.*)to(.*)")

  override def fromString(value: String): LongIntervall = {
    val matcher = pattern.matcher(value)
    if (matcher.matches) {
      val duration = new LongIntervall
      val from = matcher.group(1)
      val to = matcher.group(2)
      duration.from = java.lang.Long.valueOf(from)
      duration.to = java.lang.Long.valueOf(to) 
      return duration
    }
    null
  }

  override def toString(value: LongIntervall): String = "from" + value.from + "to" + value.to
}
