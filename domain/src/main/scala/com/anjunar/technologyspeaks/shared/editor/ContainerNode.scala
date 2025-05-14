package com.anjunar.technologyspeaks.shared.editor

import jakarta.persistence.{CascadeType, Entity, OneToMany}

import java.util
import scala.beans.BeanProperty

class ContainerNode extends Node {

  @BeanProperty
  val children : util.List[Node] = new util.ArrayList[Node]()

}
