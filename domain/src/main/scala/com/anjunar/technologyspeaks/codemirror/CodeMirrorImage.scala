package com.anjunar.technologyspeaks.codemirror

import jakarta.persistence.{Basic, Column, Entity, Lob}

import scala.compiletime.uninitialized

@Entity
class CodeMirrorImage extends AbstractCodeMirrorFile {

  @Basic
  @Lob
  var data: Array[Byte] = uninitialized

  @Basic
  var contentType : String = uninitialized

}
