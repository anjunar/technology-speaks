package com.anjunar.technologyspeaks.configuration.jaxrs

import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.ws.rs.ext.{Provider, WriterInterceptor, WriterInterceptorContext}

import scala.compiletime.uninitialized

@Provider
class FlushWriterInterceptor extends WriterInterceptor {

  @Inject
  var entityManager: EntityManager = uninitialized

  override def aroundWriteTo(context: WriterInterceptorContext): Unit = {

    if (entityManager.isJoinedToTransaction) {
      entityManager.flush()
    }

    context.proceed()
  }

}
