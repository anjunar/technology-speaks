package com.anjunar.technologyspeaks.shared.property

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.control.{Group, User}
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.security.SecurityUser
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{Column, Entity, ManyToMany, ManyToOne}
import jakarta.validation.constraints.Size

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class ManagedProperty extends AbstractEntity with OwnerProvider {

  @ManyToOne(optional = false)
  @BeanProperty
  var view : EntityView = uninitialized

  @Size(min = 1, max = 80)
  @Column(nullable = false)
  @BeanProperty
  var value : String = uninitialized

  @Column(nullable = false)
  @BeanProperty
  @Descriptor(title = "Visible for all")
  var visibleForAll : Boolean = false

  @ManyToMany
  @BeanProperty
  @Descriptor(title = "Allowed Groups")
  val groups : util.Set[Group] = new util.HashSet[Group]()

  @ManyToMany
  @BeanProperty
  @Descriptor(title = "Allowed Users")
  val users : util.Set[User] = new util.HashSet[User]()

  override def owner: SecurityUser = view.user
}

object ManagedProperty extends RepositoryContext[ManagedProperty](classOf[ManagedProperty])
