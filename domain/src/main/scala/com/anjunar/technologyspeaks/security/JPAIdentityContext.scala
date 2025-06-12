package com.anjunar.technologyspeaks.security

import com.anjunar.technologyspeaks.control.{Credential, CredentialWebAuthn, User}
import jakarta.enterprise.context.{RequestScoped, SessionScoped}
import jakarta.inject.Inject
import jakarta.security.enterprise.SecurityContext

import java.io.Serializable
import java.util.{Base64, Objects, UUID}
import scala.compiletime.uninitialized


@RequestScoped
class JPAIdentityContext extends IdentityContext with Serializable {

  @Inject var securityContext: SecurityContext = uninitialized

  private var user : SecurityCredential = uninitialized

  override def getPrincipal: SecurityCredential = {
    try
      if (Objects.isNull(user)) {
        user = Credential.find(UUID.fromString(securityContext.getCallerPrincipal.getName))
        user
      } else {
        user
      }
    catch
      case e : Exception => null
  }
  
}
