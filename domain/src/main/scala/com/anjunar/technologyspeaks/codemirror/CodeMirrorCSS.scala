package com.anjunar.technologyspeaks.codemirror

import jakarta.persistence.{Basic, Column, Entity, Lob}

import scala.compiletime.uninitialized

@Entity
class CodeMirrorCSS extends AbstractCodeMirrorFile {

  @Basic
  @Lob
  @Column(columnDefinition = "text")
  var content: String = uninitialized

}
