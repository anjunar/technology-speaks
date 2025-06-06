package com.anjunar.technologyspeaks.shared.editor

import jakarta.persistence.{Entity, Lob}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Text extends TextNode {
  
  override def toString = s"Text(${super.toString})"
}
