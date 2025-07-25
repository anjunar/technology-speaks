package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{Basic, Entity, NoResultException, OneToMany, Table}

import java.util
import java.util.UUID
import scala.compiletime.uninitialized

@Entity
@Table(name = "codemirror-tag")
class CodeMirrorTag extends AbstractEntity {

  @Basic
  @PropertyDescriptor(title = "Name", writeable = true)
  var name : String = uninitialized

  @PropertyDescriptor(title = "Files", writeable = true)
  @OneToMany(targetEntity = classOf[AbstractCodeMirrorFile])
  val files : util.Set[AbstractCodeMirrorFile] = new util.HashSet[AbstractCodeMirrorFile]()

}

object CodeMirrorTag extends RepositoryContext[CodeMirrorTag](classOf[CodeMirrorTag]) {

  def findNewest(value : String) : CodeMirrorTag = value match {
    case v : String if v == "head" =>
      try {
        entityManager.createQuery("SELECT e FROM CodeMirrorTag e ORDER BY e.created DESC", classOf[CodeMirrorTag])
          .setMaxResults(1)
          .getSingleResult
      } catch {
        case e : NoResultException => null
      }
    case _ =>
      find(UUID.fromString(value))
  }

}