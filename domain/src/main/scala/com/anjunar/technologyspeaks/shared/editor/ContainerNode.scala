package com.anjunar.technologyspeaks.shared.editor

import jakarta.persistence.{CascadeType, Entity, OneToMany}

import java.util
import scala.beans.BeanProperty
import scala.jdk.CollectionConverters.*

class ContainerNode extends Node {

  val children : util.List[Node] = new util.ArrayList[Node]()

  private def canEqual(other: Any): Boolean = other.isInstanceOf[ContainerNode]

  override def equals(other: Any): Boolean = other match {
    case that: ContainerNode =>
      that.canEqual(this) &&
        children == that.children
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(children)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"${children.asScala.mkString(", ")}, ${super.toString}"

}
