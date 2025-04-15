package com.anjunar.technologyspeaks.configuration.jaxrs.exception

import com.anjunar.scala.mapper.exceptions.ValidationException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.transaction.UserTransaction
import jakarta.validation.ConstraintViolationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import jakarta.ws.rs.ext.{ExceptionMapper, Provider}
import org.slf4j.LoggerFactory

import java.util
import scala.compiletime.uninitialized

@Provider
class ValidationExceptionWrapper extends ExceptionMapper[ValidationException] {

  private val log = LoggerFactory.getLogger(classOf[ValidationExceptionWrapper])

  @Inject var transaction: UserTransaction = uninitialized

  override def toResponse(exception: ValidationException): Response = {
    
    log.error(exception.getMessage)

    transaction.rollback()

    val violations = exception.violations

    val constraintViolations = violations.stream().map(violation => {
      val path = violation.path
      Violation(violation.message, violation.root.getSimpleName, path)
    }).toList

    val objectMapper = new ObjectMapper()

    Response
      .status(Status.BAD_REQUEST)
      .entity(objectMapper.writeValueAsString(constraintViolations))
      .build()
  }



}
