package com.anjunar.technologyspeaks.configuration.jaxrs.exception

import com.anjunar.technologyspeaks.configuration.jaxrs.ObjectMapperContextResolver
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.transaction.UserTransaction
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import jakarta.ws.rs.ext.{ExceptionMapper, Provider}
import org.slf4j.LoggerFactory

import scala.compiletime.uninitialized


@Provider
class JaxRSExceptionWrapper extends ExceptionMapper[Exception] {

  private val log = LoggerFactory.getLogger(classOf[JaxRSExceptionWrapper])

  @Inject var transaction: UserTransaction = uninitialized

  override def toResponse(exception: Exception): Response = exception match
    case web : WebApplicationException if web.getResponse.getStatus == 403 => Response
      .status(Status.FORBIDDEN)
      .entity("""[{ "message" : "Falsche Email", "clazz" : "User", "path" : ["email"]  }, { "message" : "Falsches Passwort", "clazz" : "User", "path" : ["password"]  }]""")
      .build()
    
    case _ =>
      log.error(exception.getMessage, exception)
      val objectMapper = ObjectMapperContextResolver.objectMapper
      transaction.rollback()
      Response.serverError.entity(objectMapper.writeValueAsString(exception)).build

}
