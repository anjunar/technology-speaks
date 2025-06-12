package com.anjunar.technologyspeaks.configuration.jaxrs

import com.anjunar.scala.mapper.IdProvider
import com.anjunar.scala.mapper.annotations.SecuredOwner
import com.anjunar.technologyspeaks.configuration.jaxrs.converter.*
import com.anjunar.technologyspeaks.jaxrs.types.{DateDuration, DateTimeDuration, LongIntervall, OwnerProvider}
import com.anjunar.technologyspeaks.security.IdentityContext
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response.Status
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

  @Inject
  var identityContext : IdentityContext = uninitialized
  
  override def getConverter[T](rawType: Class[T], genericType: Type, annotations: Array[Annotation]): ParamConverter[T] = {

    if (classOf[IdProvider].isAssignableFrom(rawType)) {
      return new ParamConverter[T] {
        override def fromString(value: String): T = {
          val id = UUID.fromString(value)
          val entity = entityManager.find(rawType, id)

          val securedOwner = annotations.find(annotation => annotation.annotationType() == classOf[SecuredOwner])
          if (securedOwner.isDefined) {
            val ownerProvider = entity.asInstanceOf[OwnerProvider]
            if (ownerProvider.owner != identityContext.getPrincipal) {
              throw new WebApplicationException(Status.FORBIDDEN)
            }
          }

          entity
        }

        override def toString(value: T): String = {
          value.asInstanceOf[IdProvider].id.toString
        }
      }
    }

    if (rawType == classOf[Locale]) return new LocalParamConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[DateTimeDuration]) return new DateTimeDurationConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[DateDuration]) return new DateDurationConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[LongIntervall]) return new NumberIntervallConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[LocalDate]) return new LocalDateParamConverter().asInstanceOf[ParamConverter[T]]
    if (rawType == classOf[LocalDateTime]) return new LocalDateTimeParamConverter().asInstanceOf[ParamConverter[T]]
    null
  }
}
