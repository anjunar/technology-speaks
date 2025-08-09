package com.anjunar.technologyspeaks.security

import com.google.common.collect.Sets
import com.yubico.webauthn.*
import com.yubico.webauthn.data.RelyingPartyIdentity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

import scala.compiletime.uninitialized

@ApplicationScoped
class WebAuthnService {

  @Inject
  var credentialRepository : WebAuthnCredentialRepository = uninitialized
  
  private val relyingPartyIdentity: RelyingPartyIdentity = RelyingPartyIdentity.builder()
    .id("technologyspeaks.com")
    .name("Technology Speaks")
    .build()

  lazy val relyingParty: RelyingParty = RelyingParty.builder()
    .identity(relyingPartyIdentity)
    .credentialRepository(credentialRepository)
    .origins(Sets.newHashSet("https://technologyspeaks.com"))
    .build()

}
