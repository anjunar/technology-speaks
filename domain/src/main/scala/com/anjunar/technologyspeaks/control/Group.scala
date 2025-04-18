package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.security.SecurityUser
import jakarta.persistence.{Entity, ManyToMany, ManyToOne, Table}
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.validation.constraints.Size

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import java.util

@Entity
@Table(name = "groups")
class Group extends AbstractEntity with OwnerProvider {

  @BeanProperty
  @Size(min = 3, max = 80)
  @Descriptor(title = "Name")
  var name : String = uninitialized

  @BeanProperty
  @Size(min = 0, max = 80)
  @Descriptor(title = "Description")
  var description : String = uninitialized

  @ManyToOne(optional = false)
  @BeanProperty
  var user : User = uninitialized

  @ManyToMany
  @BeanProperty
  @Descriptor(title = "Users")
  val users : util.Set[User] = new util.HashSet[User]()

  override def owner : SecurityUser = user

}
