package com.anjunar.technologyspeaks.security

import com.anjunar.technologyspeaks.control.Credential
import com.yubico.webauthn.data.{ByteArray, PublicKeyCredentialDescriptor, PublicKeyCredentialType}
import com.yubico.webauthn.{CredentialRepository, RegisteredCredential}
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager

import java.util
import java.util.stream.Collectors
import java.util.{Base64, Optional}
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

@ApplicationScoped
class WebAuthnCredentialRepository extends CredentialRepository {

  override def getCredentialIdsForUsername(username: String): util.Set[PublicKeyCredentialDescriptor] = {
    val resultList = Credential.forEmail(username)

    resultList.stream().map { credIdBytes =>
      PublicKeyCredentialDescriptor.builder()
        .id(new ByteArray(credIdBytes))
        .`type`(PublicKeyCredentialType.PUBLIC_KEY)
        .build()
    }.collect(Collectors.toSet)
  }

  override def getUserHandleForUsername(username: String): Optional[ByteArray] = {
    val entity = Credential.forUserName(username)
      .stream()
      .findFirst()

    entity.map(credential => new ByteArray(credential.email.handle))
  }

  override def getUsernameForUserHandle(userHandle: ByteArray): Optional[String] = {
    val handleStr = userHandle.getBase64Url
    val entity = Credential.find(handleStr.getBytes)
    Optional.ofNullable(entity).map(_.email.value)
  }

  override def lookup(credentialId: ByteArray, userHandle: ByteArray): Optional[RegisteredCredential] = {
    val resultList = Credential.forCredentialIdAndId(credentialId, userHandle)
    
    resultList.stream().findFirst().map { entity =>
      RegisteredCredential.builder()
        .credentialId(new ByteArray(entity.credentialId))
        .userHandle(new ByteArray(entity.email.handle))
        .publicKeyCose(new ByteArray(entity.publicKeyCose))
        .signatureCount(entity.signCount)
        .build()
    }
  }

  override def lookupAll(credentialId: ByteArray): util.Set[RegisteredCredential] = {
    val resultList = Credential.forCredentialId(credentialId)
    
    resultList.stream().map { entity =>
      RegisteredCredential.builder()
        .credentialId(new ByteArray(entity.credentialId))
        .userHandle(new ByteArray(entity.email.handle))
        .publicKeyCose(new ByteArray(entity.publicKeyCose))
        .signatureCount(entity.signCount)
        .build()
    }.collect(Collectors.toSet)
  }
}
