package com.anjunar.technologyspeaks.shared.editor

import jakarta.persistence.{Entity, Lob}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Code extends TextNode {

  @BeanProperty
  var lang : String = uninitialized
  
  private def canEqual(other: Any): Boolean = other.isInstanceOf[Code]
  
  override def equals(other: Any): Boolean = other match {
    case that: Code =>
      super.equals(that) &&
        that.canEqual(this) &&
        lang == that.lang
    case _ => false
  }
  
  override def hashCode(): Int = {
    val state = Seq(super.hashCode(), lang)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
