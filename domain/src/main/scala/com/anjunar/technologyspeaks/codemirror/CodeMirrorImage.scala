package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import jakarta.persistence.{Basic, Column, Entity, Lob}

import scala.compiletime.uninitialized

@Entity
class CodeMirrorImage extends AbstractCodeMirrorFile {

  @Basic
  @Lob
  @PropertyDescriptor(title = "Data", writeable = true)
  var data: Array[Byte] = uninitialized

  @Basic
  @PropertyDescriptor(title = "Content Type", writeable = true)
  var contentType : String = uninitialized

}
