package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.IgnoreFilter
import jakarta.persistence.{CascadeType, Entity, Inheritance, InheritanceType, OneToMany}

import scala.beans.BeanProperty
import java.util
import scala.compiletime.uninitialized

@Entity
abstract class AbstractContainerNode[C <: AbstractNode] extends AbstractNode {

  @BeanProperty
  var justify : String = uninitialized

  @BeanProperty
  @OneToMany(targetEntity = classOf[AbstractNode], cascade = Array(CascadeType.ALL), orphanRemoval = true)
  val children : util.List[C] = new util.ArrayList[C]()

}
