package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.scala.mapper.file.File
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.{Entity, Lob, Table}
import org.hibernate.envers.Audited

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity(name = "File")
@Audited
@Table(name = "File")
class EditorFile extends AbstractEntity with File {

  var contentType: String = uninitialized

  var name: String = uninitialized

  @Lob
  var data: Array[Byte] = uninitialized

  override def toString = s"EditorFile($contentType, $name)"
}
