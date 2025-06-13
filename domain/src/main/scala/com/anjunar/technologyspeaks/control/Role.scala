package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.security.SecurityRole
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.technologyspeaks.shared.validators.Unique
import jakarta.persistence.{Basic, Entity, Table, UniqueConstraint}
import jakarta.validation.constraints.{NotBlank, Size}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized


@Entity
@Table(name = "role", uniqueConstraints = Array(new UniqueConstraint(columnNames = Array("name"))))
@Unique(message = "Rolle schon vorhanden", property = "name")
class Role extends AbstractEntity with SecurityRole {

  @Size(min = 3, max = 80)
  @NotBlank
  @PropertyDescriptor(title = "Name", naming = true)
  @Basic
  var name: String = uninitialized

  @Size(min = 3, max = 80)
  @NotBlank
  @PropertyDescriptor(title = "Description")
  @Basic
  var description: String = uninitialized
  
  override def toString = s"Role($name, $description)"
}

object Role extends RepositoryContext[Role](classOf[Role])
