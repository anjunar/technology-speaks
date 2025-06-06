package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.shared.validators.Unique
import jakarta.persistence.{CascadeType, Column, Entity, ManyToOne, OneToMany, Table, UniqueConstraint}
import jakarta.validation.constraints.{Email, NotBlank}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import java.util

@Entity
@Unique(message = "Email all ready exists", property = "value")
class EMail extends AbstractEntity {

  @Email
  @NotBlank
  @Descriptor(title = "Email", naming = true, widget = "email")
  @Column(unique = true)
  @BeanProperty
  var value: String = uninitialized

  @BeanProperty
  var handle: Array[Byte] = uninitialized

  @ManyToOne(optional = false)
  @BeanProperty
  var user : User = uninitialized

  @BeanProperty
  @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true, mappedBy = "email")  
  val credentials : util.Set[Credential] = new util.HashSet[Credential]()
  
  override def toString = s"EMail($value)"
}

object EMail extends RepositoryContext[EMail](classOf[EMail])