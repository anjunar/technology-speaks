package com.anjunar.technologyspeaks.shared.editor

import jakarta.persistence.{Entity, Lob}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class TextNode extends Node {

  @BeanProperty
  var value: String = uninitialized
  
  private def canEqual(other: Any): Boolean = other.isInstanceOf[TextNode]
  
  override def equals(other: Any): Boolean = other match {
    case that: TextNode =>
      that.canEqual(this) &&
        value == that.value
    case _ => false
  }
  
  override def hashCode(): Int = if value == null then 0 else value.hashCode
  
  override def toString = s"TextNode($value, ${super.toString})"
}
