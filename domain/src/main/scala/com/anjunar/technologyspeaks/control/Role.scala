package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.security.SecurityRole
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.technologyspeaks.shared.validators.Unique
import com.anjunar.scala.mapper.annotations.Descriptor
import jakarta.persistence.{Entity, Table, UniqueConstraint}
import jakarta.validation.constraints.{NotBlank, Size}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized


@Entity
@Table(name = "role", uniqueConstraints = Array(new UniqueConstraint(columnNames = Array("name"))))
@Unique(message = "Rolle schon vorhanden", property = "name")
class Role extends AbstractEntity with SecurityRole {

  @Size(min = 3, max = 80)
  @NotBlank
  @BeanProperty
  @Descriptor(title = "Name", naming = true)
  var name: String = uninitialized

  @Size(min = 3, max = 80)
  @NotBlank
  @BeanProperty
  @Descriptor(title = "Beschreibung")
  var description: String = uninitialized

}

object Role extends RepositoryContext[Role](classOf[Role])
