package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.*
import jakarta.validation.constraints.{Email, NotBlank}

import java.util
import scala.compiletime.uninitialized

@Entity
class EMail extends AbstractEntity {

  @Email
  @NotBlank
  @PropertyDescriptor(title = "Email", naming = true, widget = "email")
  @Column(unique = true)
  @Basic
  var value: String = uninitialized

  @Basic
  var handle: Array[Byte] = uninitialized

  @ManyToOne(optional = false, targetEntity = classOf[User])
  var user: User = uninitialized

  @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true, mappedBy = "email", targetEntity = classOf[Credential])
  val credentials: util.Set[Credential] = new util.HashSet[Credential]()

  override def toString = s"EMail($value)"
}

object EMail extends RepositoryContext[EMail](classOf[EMail])