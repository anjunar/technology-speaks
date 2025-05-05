package com.anjunar.scala.universe

import scala.beans.BeanProperty
import scala.collection.mutable.ArrayBuffer

class AbstractContainerNode[C <: AbstractNode] extends AbstractNode {

  @BeanProperty
  val children: Seq[C] = Seq.empty


}
