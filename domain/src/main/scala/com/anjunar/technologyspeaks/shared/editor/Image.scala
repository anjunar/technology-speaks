package com.anjunar.technologyspeaks.shared.editor

import jakarta.persistence.Entity

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Image extends Node {

  @BeanProperty
  var url : String = uninitialized

  @BeanProperty
  var alt : String = uninitialized

  private def canEqual(other: Any): Boolean = other.isInstanceOf[Image]

  override def equals(other: Any): Boolean = other match {
    case that: Image =>
      that.canEqual(this) &&
        url == that.url &&
        alt == that.alt
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(url, alt)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
