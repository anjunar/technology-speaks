package com.anjunar.technologyspeaks.codemirror

import jakarta.persistence.{Basic, Column, Entity, Lob}

import scala.compiletime.uninitialized

@Entity
class CodeMirrorHTML extends AbstractCodeMirrorFile {

  @Basic
  @Lob
  @Column(columnDefinition = "text")
  var content: String = uninitialized

}
