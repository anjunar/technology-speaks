package com.anjunar.technologyspeaks.codemirror

import jakarta.persistence.{Basic, Entity}

import scala.compiletime.uninitialized

@Entity
class CodeMirrorImage extends AbstractCodeMirrorFile {
  
  @Basic
  var contentType : String = uninitialized

}
