package com.anjunar.technologyspeaks.codemirror

import jakarta.persistence.{Basic, Column, Entity, Lob}

import scala.compiletime.uninitialized

@Entity
class CodeMirrorTS extends AbstractCodeMirrorFile {

  @Basic
  @Lob
  @Column(columnDefinition = "text")
  var content: String = uninitialized

  @Basic
  @Lob
  @Column(columnDefinition = "text")
  var transpiled: String = uninitialized

  @Basic
  @Lob
  @Column(columnDefinition = "text")
  var sourceMap: String = uninitialized
  
}
