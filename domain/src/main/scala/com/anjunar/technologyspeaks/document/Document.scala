package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.security.SecurityUser
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.technologyspeaks.shared.editor.RootNode
import jakarta.persistence.{CascadeType, Column, Entity, ManyToOne, OneToOne}
import jakarta.validation.constraints.Size
import org.hibernate.`type`.SqlTypes
import org.hibernate.annotations.JdbcTypeCode

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import org.hibernate.annotations

@Entity
class Document extends AbstractEntity with OwnerProvider {

  @Size(min = 3, max = 80)
  @BeanProperty
  var title : String = uninitialized

  @Descriptor(title = "User")
  @ManyToOne(optional = false)
  @BeanProperty
  var user: User = uninitialized

  @Descriptor(title = "Root", widget = "editor")
  @OneToOne(optional = false, cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  var root: RootNode = uninitialized

  @Column
  @JdbcTypeCode(SqlTypes.VECTOR)
  @annotations.Array(length = 3072)
  @BeanProperty
  var embedding: Array[Float] = uninitialized

  override def owner: SecurityUser = user

}

object Document extends RepositoryContext[Document](classOf[Document])
