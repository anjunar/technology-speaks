package com.anjunar.technologyspeaks.security

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.security.enterprise.authentication.mechanism.http.{AutoApplySession, HttpAuthenticationMechanism, HttpMessageContext, RememberMe}
import jakarta.security.enterprise.credential.Credential
import jakarta.security.enterprise.identitystore.IdentityStoreHandler
import jakarta.security.enterprise.{AuthenticationException, AuthenticationStatus}
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}


@AutoApplySession
@ApplicationScoped
class CustomAuthentication @Inject()(private val identityStoreHandler: IdentityStoreHandler) extends HttpAuthenticationMechanism {

  def this() = {
    this(null)
  }

  @throws[AuthenticationException]
  override def validateRequest(request: HttpServletRequest, response: HttpServletResponse, httpMessageContext: HttpMessageContext): AuthenticationStatus = {
    val credential = request.getAttribute("credential").asInstanceOf[Credential]
    if (credential != null) {
      val result = identityStoreHandler.validate(credential)
      return httpMessageContext.notifyContainerAboutLogin(result)
    }
    if (httpMessageContext.isProtected)
      return httpMessageContext.responseUnauthorized
    httpMessageContext.doNothing
  }

  override def cleanSubject(request: HttpServletRequest, response: HttpServletResponse, httpMessageContext: HttpMessageContext): Unit = {
    httpMessageContext.cleanClientSubject()
  }
}
