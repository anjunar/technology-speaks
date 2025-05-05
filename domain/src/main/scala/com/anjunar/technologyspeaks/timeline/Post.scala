package com.anjunar.technologyspeaks.timeline

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.technologyspeaks.shared.editor.RootNode
import jakarta.persistence.{CascadeType, Entity, ManyToOne, OneToOne}
import com.anjunar.technologyspeaks.jpa.{RepositoryContext, Save}
import com.anjunar.technologyspeaks.security.SecurityUser

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class Post extends AbstractEntity with OwnerProvider {

  @Descriptor(title = "User")
  @ManyToOne(optional = false)
  @BeanProperty
  var user : User = uninitialized

  @Descriptor(title = "Root", widget = "editor")
  @OneToOne(optional = false, cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  var root : RootNode = uninitialized

  override def owner: SecurityUser = user
}

object Post extends RepositoryContext[Post](classOf[Post])
