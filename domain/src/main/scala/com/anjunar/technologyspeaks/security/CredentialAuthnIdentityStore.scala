package com.anjunar.technologyspeaks.security

import com.anjunar.technologyspeaks.control.{CredentialPassword, CredentialWebAuthn, User}
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.security.enterprise.credential.{Credential, UsernamePasswordCredential}
import jakarta.security.enterprise.identitystore.{CredentialValidationResult, IdentityStore}

import java.util
import java.util.stream.Collectors

@ApplicationScoped
class CredentialAuthnIdentityStore @Inject()(webAuthnService: WebAuthnService) extends IdentityStore {

  override def validate(credential: Credential): CredentialValidationResult = {
    credential match {
      case usernamePassword : UsernamePasswordCredential =>
        val user = User.findByEmail(usernamePassword.getCaller)

        val validated = user.emails.stream()
          .flatMap(email => email.credentials.stream())
          .filter({
            case webAuthnCredential: CredentialPassword => webAuthnCredential.password == usernamePassword.getPasswordAsString
            case _ => false
          })
          .findFirst()

        if (validated.isPresent) {
          new CredentialValidationResult(validated.get().id.toString, validated.get().roles.stream().map(role => role.name).collect(Collectors.toSet))
        } else {
          CredentialValidationResult.INVALID_RESULT
        }
      case web: WebAuthnCredential =>
        val maybeCredential = webAuthnService
          .credentialRepository
          .lookup(web.credentialId, web.userHandle)

        if (maybeCredential.isPresent) {

          val user = User.findByEmail(web.username)
          
          val validated = user.emails.stream()
            .flatMap(email => email.credentials.stream())
            .filter({
              case webAuthnCredential: CredentialWebAuthn => util.Arrays.equals(webAuthnCredential.credentialId, web.credentialId.getBytes)
              case _ => false  
            })
            .findFirst()
          
          if (validated.isPresent) {
            new CredentialValidationResult(validated.get().id.toString, validated.get().roles.stream().map(role => role.name).collect(Collectors.toSet))
          } else {
            CredentialValidationResult.INVALID_RESULT
          }
        } else {
          CredentialValidationResult.INVALID_RESULT
        }
      case _ => CredentialValidationResult.NOT_VALIDATED_RESULT
    }
  }
}
