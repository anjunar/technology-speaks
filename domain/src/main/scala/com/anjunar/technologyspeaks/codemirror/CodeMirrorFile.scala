package com.anjunar.technologyspeaks.codemirror

import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{Basic, Column, Entity, Lob}

import scala.compiletime.uninitialized

@Entity
class CodeMirrorFile extends AbstractEntity {

  @Basic
  @Column(unique = true)
  var name : String = uninitialized

  @Basic
  @Lob
  @Column(columnDefinition = "text")
  var content : String = uninitialized

  @Basic
  @Lob
  @Column(columnDefinition = "text")
  var transpiled : String = uninitialized

}

object CodeMirrorFile extends RepositoryContext[CodeMirrorFile](classOf[CodeMirrorFile]) {

  def findByName(name: String) : CodeMirrorFile = {
    entityManager.createQuery("SELECT e FROM CodeMirrorFile e WHERE e.name like :name", classOf[CodeMirrorFile])
      .setParameter("name", name + "%")
      .getSingleResult
  }

}