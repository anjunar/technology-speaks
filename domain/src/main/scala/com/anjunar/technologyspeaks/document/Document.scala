package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.security.SecurityUser
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.technologyspeaks.shared.editor.{Editor, Root}
import jakarta.persistence.{CascadeType, Column, Entity, Lob, ManyToOne, OneToMany, OneToOne, Transient}
import jakarta.validation.constraints.Size
import org.hibernate.`type`.SqlTypes
import org.hibernate.annotations.JdbcTypeCode

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import java.util

@Entity
class Document extends AbstractEntity with OwnerProvider {

  @Size(min = 3, max = 80)
  @Descriptor(title = "Title")
  @BeanProperty
  var title : String = uninitialized

  @Lob
  @Descriptor(title = "Description")
  @BeanProperty
  var description : String = uninitialized

  @Descriptor(title = "User")
  @ManyToOne(optional = false)
  @BeanProperty
  var user: User = uninitialized

  @Descriptor(title = "Editor", widget = "editor")
  @OneToOne(optional = false, cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  var editor : Editor = uninitialized

  @Descriptor(title = "Score")
  @Transient
  @BeanProperty
  var score: Double = uninitialized

  @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true, mappedBy = "document")
  @BeanProperty
  val chunks : util.List[Chunk] = new util.ArrayList[Chunk]()

  override def owner: SecurityUser = user

}

object Document extends RepositoryContext[Document](classOf[Document])
