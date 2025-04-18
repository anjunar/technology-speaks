package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.control.{Group, User}
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import jakarta.persistence.{Column, Entity, ManyToMany, ManyToOne}
import jakarta.validation.constraints.Size

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import java.util

@Entity
class ManagedProperty extends AbstractEntity {

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

}

object ManagedProperty extends RepositoryContext[ManagedProperty](classOf[ManagedProperty])
