package com.anjunar.technologyspeaks.configuration.jaxrs

import com.anjunar.technologyspeaks.configuration.jaxrs.converter.*
import com.anjunar.technologyspeaks.jaxrs.types.{DateDuration, DateTimeDuration, LongIntervall}
import jakarta.ws.rs.ext.{ParamConverter, ParamConverterProvider, Provider}

import java.lang.annotation.Annotation
import java.lang.reflect.Type
import java.time.{LocalDate, LocalDateTime}
import java.util.Locale


@Provider 
class CustomParamConverterProvider extends ParamConverterProvider {
  
  override def getConverter[T](rawType: Class[T], genericType: Type, annotations: Array[Annotation]): ParamConverter[T] = {
    if (rawType == classOf[Locale]) return new LocalParamConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[DateTimeDuration]) return new DateTimeDurationConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[DateDuration]) return new DateDurationConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[LongIntervall]) return new NumberIntervallConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[LocalDate]) return new LocalDateParamConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[LocalDateTime]) return new LocalDateTimeParamConverter().asInstanceOf[ParamConverter[T]]
    null
  }
}
