package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.yubico.webauthn.data.ByteArray
import jakarta.persistence.{Entity, NoResultException}

import java.security.SecureRandom
import java.util
import java.util.Base64
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class CredentialWebAuthn extends Credential {

  @BeanProperty
  @Descriptor(title = "Device Name", widget = "text")
  var displayName: String = uninitialized

  @BeanProperty
  var credentialId: Array[Byte] = uninitialized

  @BeanProperty
  var publicKeyCose: Array[Byte] = uninitialized

  @BeanProperty
  var signCount: Long = uninitialized

  @BeanProperty
  var transports: String = uninitialized

  @BeanProperty
  var oneTimeToken: String = uninitialized

  def generateOneTimeToken(): Unit = {
    val bytes = new Array[Byte](32)
    val random = new SecureRandom()
    random.nextBytes(bytes)
    oneTimeToken = new ByteArray(bytes).getBase64
  }

  override def toString = s"WebAuthnCredential($displayName, $signCount, $transports)"

}

object CredentialWebAuthn extends RepositoryContext[CredentialWebAuthn](classOf[CredentialWebAuthn]) {

  def findByCredentialId(value : String): CredentialWebAuthn = {
    try
      entityManager.createQuery("select t from CredentialWebAuthn t where t.credentialId = :value", classOf[CredentialWebAuthn])
        .setParameter("value", Base64.getDecoder.decode(value))
        .getSingleResult
    catch
      case e : NoResultException => null
  }

  def forEmail(email: String): util.List[Array[Byte]] = {
    entityManager.createQuery("SELECT c.credentialId FROM CredentialWebAuthn c join c.email e WHERE e.value = :email and c.credentialId is not null", classOf[Array[Byte]])
      .setParameter("email", email)
      .getResultList
  }

  def forUserName(email: String): util.List[CredentialWebAuthn] = {
    entityManager.createQuery("SELECT c FROM CredentialWebAuthn c join c.email e WHERE e.value = :email", classOf[CredentialWebAuthn])
      .setParameter("email", email)
      .setMaxResults(1)
      .getResultList
  }

  def forUserNameAndDisplayName(email: String, displayName: String): CredentialWebAuthn = {
    try
      entityManager.createQuery("SELECT c FROM CredentialWebAuthn c join c.email e WHERE e.value = :email and c.displayName = :displayName", classOf[CredentialWebAuthn])
        .setParameter("email", email)
        .setParameter("displayName", displayName)
        .getSingleResult
    catch
      case e: NoResultException => null
  }


  def forCredentialIdAndId(credentialId: ByteArray, userHandle: ByteArray): util.List[CredentialWebAuthn] = {
    entityManager.createQuery("SELECT c FROM CredentialWebAuthn c join c.email e WHERE c.credentialId = :credId AND e.handle = :id", classOf[CredentialWebAuthn])
      .setParameter("credId", credentialId.getBytes)
      .setParameter("id", userHandle.getBytes)
      .getResultList
  }

  def forCredentialId(credentialId: ByteArray): util.List[CredentialWebAuthn] = {
    entityManager.createQuery("SELECT c FROM CredentialWebAuthn c WHERE c.credentialId = :credId", classOf[CredentialWebAuthn])
      .setParameter("credId", credentialId.getBytes)
      .getResultList
  }


}