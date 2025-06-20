package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import jakarta.persistence.{Basic, Column, Entity, Inheritance, InheritanceType, Lob, NoResultException, Table}
import jakarta.validation.constraints.{NotBlank, Size}

import scala.compiletime.uninitialized

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonSubTypes(Array(
  new JsonSubTypes.Type(value = classOf[CodeMirrorCSS], name = "CodeMirrorCSS"),
  new JsonSubTypes.Type(value = classOf[CodeMirrorHTML], name = "CodeMirrorHTML"),
  new JsonSubTypes.Type(value = classOf[CodeMirrorImage], name = "CodeMirrorImage"),
  new JsonSubTypes.Type(value = classOf[CodeMirrorTS], name = "CodeMirrorTS")
))
@JsonTypeInfo(use = Id.NAME, property = "$type")
@Table(name = "codemirror-file")
abstract class AbstractCodeMirrorFile extends AbstractEntity {

  @Basic
  @Column(unique = true)
  @NotBlank
  @Size(min = 1)
  @PropertyDescriptor(title = "Name", writeable = true)
  var name : String = uninitialized

}

object AbstractCodeMirrorFile extends RepositoryContext[AbstractCodeMirrorFile](classOf[AbstractCodeMirrorFile]) {

  def findByName(name: String) : AbstractCodeMirrorFile = {
    try {
      entityManager.createQuery("SELECT e FROM AbstractCodeMirrorFile e WHERE e.name like :name", classOf[AbstractCodeMirrorFile])
        .setParameter("name", name + "%")
        .getSingleResult
    } catch {
      case e : NoResultException => null
    }
  }

}