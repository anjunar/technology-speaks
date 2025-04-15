package com.anjunar.technologyspeaks.configuration.jaxrs.converter

import jakarta.ws.rs.ext.ParamConverter

import java.util.Locale


class LocalParamConverter extends ParamConverter[Locale] {
  override def fromString(value: String): Locale = Locale.forLanguageTag(value)

  override def toString(value: Locale): String = value.toLanguageTag
}
