package com.anjunar.technologyspeaks.security

import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters
import jakarta.security.enterprise.credential.Credential
import jakarta.security.enterprise.{AuthenticationStatus, SecurityContext}
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

import java.security.Principal


@RequestScoped
class Authenticator @Inject() (private val securityContext: SecurityContext, 
                               private val request: HttpServletRequest, 
                               private val response: HttpServletResponse) {

  def this() = {
    this(null, null, null)
  }

  def authenticate(credential: Credential): AuthenticationStatus = {
    val parameters = new AuthenticationParameters
    parameters.setCredential(credential)
    parameters.setNewAuthentication(true)
    parameters.setRememberMe(true)
    request.setAttribute("credential", credential)
    securityContext.authenticate(request, response, parameters)
  }

  def logout(): Unit = {
    request.logout()
    request.getSession.invalidate()
  }

  def getUserPrincipal: Principal = request.getUserPrincipal
}
