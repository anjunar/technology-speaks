package com.anjunar.technologyspeaks.configuration

import com.anjunar.technologyspeaks.security.Secured
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.interceptor.{AroundInvoke, Interceptor, InvocationContext}
import jakarta.security.enterprise.SecurityContext
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status

import scala.compiletime.uninitialized


@Secured
@Interceptor
class SecurityInterceptor {

  @Inject var securityContext: SecurityContext = uninitialized

  @AroundInvoke
  @throws[Exception]
  def around(context: InvocationContext): AnyRef = {

    val rolesAllowed = context.getMethod.getAnnotation(classOf[RolesAllowed])

    if (rolesAllowed.value().exists(role => securityContext.isCallerInRole(role))) {
      return context.proceed()
    }

    throw new WebApplicationException(Status.FORBIDDEN)
  }
}
