package com.anjunar.technologyspeaks.shared

import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.security.SecurityUser
import jakarta.persistence.{CascadeType, Entity, Inheritance, InheritanceType, ManyToOne, MappedSuperclass, OneToMany}

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class EntityView extends AbstractEntity with OwnerProvider {

  @ManyToOne(optional = false)
  @BeanProperty
  var user : User = uninitialized

  @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  var properties : util.Set[ManagedProperty] = new util.HashSet[ManagedProperty]()

  override def owner: SecurityUser = user

}
