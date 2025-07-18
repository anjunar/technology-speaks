package com.anjunar.technologyspeaks.shared.property

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.control.{Group, User}
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.security.SecurityUser
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{Basic, Column, Entity, ManyToMany, ManyToOne}
import jakarta.validation.constraints.Size

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class ManagedProperty extends AbstractEntity with OwnerProvider {

  @ManyToOne(optional = false, targetEntity = classOf[EntityView])
  var view : EntityView = uninitialized

  @Size(min = 1, max = 80)
  @Column(nullable = false)
  @Basic
  var value : String = uninitialized

  @Column(nullable = false)
  @PropertyDescriptor(title = "Visible for all")
  @Basic
  var visibleForAll : Boolean = false

  @ManyToMany(targetEntity = classOf[Group])
  @PropertyDescriptor(title = "Allowed Groups")
  val groups : util.Set[Group] = new util.HashSet[Group]()

  @ManyToMany
  @PropertyDescriptor(title = "Allowed Users")
  val users : util.Set[User] = new util.HashSet[User]()

  override def owner: SecurityUser = view.user
  
  override def toString = s"ManagedProperty($value, $visibleForAll)"
}

object ManagedProperty extends RepositoryContext[ManagedProperty](classOf[ManagedProperty]) {

  def manage(currentUser: User, isOwnedOrAdmin: Boolean, view: EntityView, name: String): (Boolean, UUID) = {
    if (view == null) {
      return (true, null)
    }
    val managedProperty = view.properties
      .stream()
      .filter(property => property.value == name)
      .findFirst()
      .orElseGet(() => {
        val property = new ManagedProperty()
        property.value = name
        property.view = view
        property.saveOrUpdate()
        view.properties.add(property)
        property
      })

    if (isOwnedOrAdmin) {
      (true, managedProperty.id)
    } else {
      if (managedProperty.visibleForAll) {
        (true, null)
      } else {
        (managedProperty.users.contains(currentUser) || managedProperty.groups.stream().anyMatch(group => group.users.contains(currentUser)), null)
      }
    }
  }


}
