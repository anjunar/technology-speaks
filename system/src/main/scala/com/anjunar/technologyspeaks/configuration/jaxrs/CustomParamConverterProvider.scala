package com.anjunar.technologyspeaks.configuration.jaxrs

import com.anjunar.technologyspeaks.configuration.jaxrs.converter.*
import com.anjunar.technologyspeaks.jaxrs.types.{DateDuration, DateTimeDuration, IdProvider, LongIntervall}
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.ws.rs.ext.{ParamConverter, ParamConverterProvider, Provider}

import java.lang.annotation.Annotation
import java.lang.reflect.Type
import java.time.{LocalDate, LocalDateTime}
import java.util
import java.util.{Locale, UUID}
import scala.compiletime.uninitialized


@Provider 
class CustomParamConverterProvider extends ParamConverterProvider {

  @Inject
  var entityManager: EntityManager = uninitialized
  
  override def getConverter[T](rawType: Class[T], genericType: Type, annotations: Array[Annotation]): ParamConverter[T] = {

    if (classOf[IdProvider].isAssignableFrom(rawType)) {
      return new ParamConverter[T] {
        override def fromString(value: String): T = {
          val id = UUID.fromString(value)
          entityManager.find(rawType, id)
        }

        override def toString(value: T): String = {
          value.asInstanceOf[IdProvider].id.toString
        }
      }
    }

/*
    if (classOf[util.List[?]].isAssignableFrom(rawType)) {
      return new ParamConverter[util.List[T]] {
        override def fromString(value: String): util.List[T] = {
          null.asInstanceOf[util.List[T]]
        }

        override def toString(value: util.List[T]): String = {
          ""
        }
      }.asInstanceOf[ParamConverter[T]]
    }
*/

    if (rawType == classOf[Locale]) return new LocalParamConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[DateTimeDuration]) return new DateTimeDurationConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[DateDuration]) return new DateDurationConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[LongIntervall]) return new NumberIntervallConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[LocalDate]) return new LocalDateParamConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[LocalDateTime]) return new LocalDateTimeParamConverter().asInstanceOf[ParamConverter[T]]
    null
  }
}
