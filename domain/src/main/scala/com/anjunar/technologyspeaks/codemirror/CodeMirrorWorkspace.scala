package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.security.SecurityUser
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{Entity, ManyToOne, OneToMany, OneToOne, Table}

import java.util
import scala.compiletime.uninitialized

@Entity
@Table(name = "codemirror-workspace")
class CodeMirrorWorkspace extends AbstractEntity with OwnerProvider {

  @PropertyDescriptor(title = "Owner")
  @ManyToOne(optional = false, targetEntity = classOf[User])
  var user : User = uninitialized

  @PropertyDescriptor(title = "Open Files", writeable = true)
  @OneToMany(targetEntity = classOf[AbstractCodeMirrorFile])
  val open: util.List[AbstractCodeMirrorFile] = new util.ArrayList[AbstractCodeMirrorFile]()

  override def owner: SecurityUser = user

  override def toString = s"CodeMirrorWorkspace($user, $open)"
}

object CodeMirrorWorkspace extends RepositoryContext[CodeMirrorWorkspace](classOf[CodeMirrorWorkspace]) {

  def apply(user : User) : CodeMirrorWorkspace = {
    val newInstance = new CodeMirrorWorkspace()
    newInstance.user = user
    newInstance
  }

  def findByUser(user: User) : CodeMirrorWorkspace = query(("user", user))

}
