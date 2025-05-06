package com.anjunar.technologyspeaks.jaxrs.types

import com.anjunar.scala.mapper.annotations.Descriptor

import java.util
import java.util.List
import scala.annotation.meta.field
import scala.beans.BeanProperty


class Table[E](@BeanProperty
               @(Descriptor @field)(title = "Rows", widget = "table")
               var rows: util.List[E],
               @BeanProperty
               @(Descriptor @field)(title = "Size")
               var size: Long) {

  def this() = {
    this(null, 0L)
  }

}
