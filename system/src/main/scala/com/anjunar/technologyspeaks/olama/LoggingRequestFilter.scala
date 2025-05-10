package com.anjunar.technologyspeaks.olama

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.ws.rs.client.{ClientRequestContext, ClientRequestFilter}
import jakarta.ws.rs.ext.Provider

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

@Provider
class LoggingRequestFilter extends ClientRequestFilter {

  override def filter(requestContext: ClientRequestContext): Unit = {

    val entity = requestContext.getEntity

    val mapper = new ObjectMapper()

    println(mapper.writeValueAsString(entity))

  }
}
