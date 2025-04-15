package com.anjunar.technologyspeaks.security

import com.anjunar.technologyspeaks.control.User
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.security.enterprise.credential.Credential
import jakarta.security.enterprise.identitystore.{CredentialValidationResult, IdentityStore}

import java.util
import java.util.stream.Collectors

@ApplicationScoped
class WebAuthnIdentityStore @Inject()(webAuthnService: WebAuthnService) extends IdentityStore {

  override def validate(credential: Credential): CredentialValidationResult = {
    credential match {
      case web: WebAuthnCredential =>
        val maybeCredential = webAuthnService
          .credentialRepository
          .lookup(web.credentialId, web.userHandle)

        if (maybeCredential.isPresent) {

          val user = User.findByEmail(web.username)
          
          val validated = user.emails.stream()
            .flatMap(email => email.credentials.stream())
            .filter(token => util.Arrays.equals(token.credentialId, web.credentialId.getBytes))
            .findFirst()
          
          if (validated.isPresent) {
            new CredentialValidationResult(web.credentialId.getBase64, validated.get().roles.stream().map(role => role.name).collect(Collectors.toSet))
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
