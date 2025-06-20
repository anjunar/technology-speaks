package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import jakarta.persistence.{Basic, Column, Entity, Lob}

import scala.compiletime.uninitialized

@Entity
class CodeMirrorTS extends AbstractCodeMirrorFile {

  @Basic
  @Lob
  @Column(columnDefinition = "text")
  @PropertyDescriptor(title = "Content", writeable = true)  
  var content: String = uninitialized

  @Basic
  @Lob
  @Column(columnDefinition = "text")
  @PropertyDescriptor(title = "Transpiled", writeable = true)
  var transpiled: String = uninitialized

  @Basic
  @Lob
  @Column(columnDefinition = "text")
  @PropertyDescriptor(title = "Source Map", writeable = true)
  var sourceMap: String = uninitialized
  
}
