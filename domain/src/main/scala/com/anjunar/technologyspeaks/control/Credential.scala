package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.security.{IdentityContext, SecurityCredential, SecurityUser}
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.yubico.webauthn.data.ByteArray
import jakarta.enterprise.inject.spi.CDI
import jakarta.persistence.{CascadeType, Column, Entity, Inheritance, InheritanceType, ManyToMany, ManyToOne, NoResultException}
import jakarta.validation.constraints.Size

import java.security.SecureRandom
import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import java.util
import java.util.Base64

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
class Credential extends AbstractEntity with SecurityCredential with OwnerProvider {

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

}

object Credential extends RepositoryContext[Credential](classOf[Credential]){
  
  def current(): Credential = {
    val identityContext = CDI.current().select(classOf[IdentityContext]).select().get()
    identityContext.getPrincipal.asInstanceOf[Credential]
  }
}