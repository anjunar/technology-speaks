package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.IgnoreFilter
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.fasterxml.jackson.annotation.JsonSubTypes
import jakarta.persistence.{Entity, Inheritance, InheritanceType, Table}

import scala.beans.BeanProperty

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "editornode")
@JsonSubTypes(Array(
  new JsonSubTypes.Type(value = classOf[CodeNode]),
  new JsonSubTypes.Type(value = classOf[ImageNode]),
  new JsonSubTypes.Type(value = classOf[ListNode]),
  new JsonSubTypes.Type(value = classOf[ParagraphNode]),
  new JsonSubTypes.Type(value = classOf[TableNode])
))
abstract class AbstractNode extends AbstractEntity {

  @BeanProperty
  var domHeight : java.lang.Integer = 0

}
