package com.anjunar.technologyspeaks.security

import jakarta.security.enterprise.credential.Credential
import com.yubico.webauthn.data.ByteArray

case class WebAuthnCredential(username: String,
                              credentialId: ByteArray,
                              userHandle: ByteArray) extends Credential
