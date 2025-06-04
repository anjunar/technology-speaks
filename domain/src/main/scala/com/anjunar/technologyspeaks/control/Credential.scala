package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.security.{IdentityContext, SecurityCredential, SecurityUser}
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.yubico.webauthn.data.ByteArray
import jakarta.enterprise.inject.spi.CDI
import jakarta.persistence.{CascadeType, Column, Entity, ManyToMany, ManyToOne, NoResultException}
import jakarta.validation.constraints.Size

import java.security.SecureRandom
import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import java.util
import java.util.Base64

@Entity
class Credential extends AbstractEntity with SecurityCredential with OwnerProvider {

  @BeanProperty
  @Descriptor(title = "Device Name", widget = "text")
  var displayName : String = uninitialized

  @BeanProperty
  var credentialId: Array[Byte] = uninitialized

  @BeanProperty
  var publicKeyCose: Array[Byte] = uninitialized

  @BeanProperty
  var signCount: Long = uninitialized

  @BeanProperty
  var transports: String = uninitialized
  
  @BeanProperty
  var oneTimeToken : String = uninitialized

  @ManyToMany
  @Size(min = 1, max = 10)
  @BeanProperty
  @Descriptor(title = "Roles")
  val roles: util.Set[Role] = new util.HashSet[Role]

  @ManyToOne
  @BeanProperty  
  var email : EMail = uninitialized

  override def hasRole(name: String): Boolean = roles.stream.anyMatch((role: Role) => role.name == name)

  override def user: SecurityUser = email.user

  override def owner: SecurityUser = user

  def validated : Boolean = hasRole("User") || hasRole("Administrator")

  def generateOneTimeToken(): Unit = {
    val bytes = new Array[Byte](32)
    val random = new SecureRandom()
    random.nextBytes(bytes)
    oneTimeToken = new ByteArray(bytes).getBase64
  }
  
}

object Credential extends RepositoryContext[Credential](classOf[Credential]) {

  def current(): Credential = {
    val identityContext = CDI.current().select(classOf[IdentityContext]).select().get()
    identityContext.getPrincipal.asInstanceOf[Credential]
  }
  
  def findByCredentialId(value : String): Credential = {
    try
      entityManager.createQuery("select t from Credential t where t.credentialId = :value", classOf[Credential])
        .setParameter("value", Base64.getDecoder.decode(value))
        .getSingleResult
    catch
      case e : NoResultException => null
  }

  def forEmail(email: String): util.List[Array[Byte]] = {
    entityManager.createQuery("SELECT c.credentialId FROM Credential c join c.email e WHERE e.value = :email and c.credentialId is not null", classOf[Array[Byte]])
      .setParameter("email", email)
      .getResultList
  }

  def forUserName(email: String): util.List[Credential] = {
    entityManager.createQuery("SELECT c FROM Credential c join c.email e WHERE e.value = :email", classOf[Credential])
      .setParameter("email", email)
      .setMaxResults(1)
      .getResultList
  }

  def forUserNameAndDisplayName(email: String, displayName: String): Credential = {
    try
      entityManager.createQuery("SELECT c FROM Credential c join c.email e WHERE e.value = :email and c.displayName = :displayName", classOf[Credential])
        .setParameter("email", email)
        .setParameter("displayName", displayName)
        .getSingleResult
    catch
      case e: NoResultException => null
  }


  def forCredentialIdAndId(credentialId: ByteArray, userHandle: ByteArray): util.List[Credential] = {
    entityManager.createQuery("SELECT c FROM Credential c join c.email e WHERE c.credentialId = :credId AND e.handle = :id", classOf[Credential])
      .setParameter("credId", credentialId.getBytes)
      .setParameter("id", userHandle.getBytes)
      .getResultList
  }

  def forCredentialId(credentialId: ByteArray): util.List[Credential] = {
    entityManager.createQuery("SELECT c FROM Credential c WHERE c.credentialId = :credId", classOf[Credential])
      .setParameter("credId", credentialId.getBytes)
      .getResultList
  }


}