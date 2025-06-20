package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import jakarta.persistence.{Basic, Column, Entity, Lob}
import jakarta.validation.constraints.{NotBlank, Size}

import scala.compiletime.uninitialized

@Entity
class CodeMirrorTS extends AbstractCodeMirrorFile {

  @Basic
  @Lob
  @Column(columnDefinition = "text")
  @NotBlank
  @Size(min = 1)
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
