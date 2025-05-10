package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.IgnoreFilter
import jakarta.persistence.{Entity, Lob}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class CodeNode extends AbstractNode {

  @Lob
  @BeanProperty
  var text : String = uninitialized

}
