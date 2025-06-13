package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.{Converter, PropertyDescriptor}
import com.anjunar.scala.mapper.file.{File, FileContext}
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.*
import jakarta.ws.rs.FormParam
import org.hibernate.annotations.Type
import org.hibernate.envers.Audited

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
@Audited
class Editor extends AbstractEntity with FileContext {

  @PropertyDescriptor(title = "Files")
  @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true, targetEntity = classOf[EditorFile])
  @FormParam("files")  
  val files: util.List[File] = new util.ArrayList[File]()

  @PropertyDescriptor(title = "AST")
  @Column(columnDefinition = "jsonb")
  @Type(classOf[RootType])
  @Converter(classOf[RootConverter])
  var json: Root = uninitialized

  @Lob
  @Column(columnDefinition = "text")
  @PropertyDescriptor(title = "Markdown")
  @FormParam("editor")
  var markdown: String = uninitialized

  @Transient
  @PropertyDescriptor(title = "Changes")
  val changes: util.List[Change] = new util.ArrayList[Change]()

  override def toString = s"Editor($json)"
}
