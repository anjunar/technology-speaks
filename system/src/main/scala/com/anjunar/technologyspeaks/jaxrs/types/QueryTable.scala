package com.anjunar.technologyspeaks.jaxrs.types

import com.anjunar.scala.mapper.annotations.Descriptor

import java.util
import scala.annotation.meta.field
import scala.beans.BeanProperty

class QueryTable[S, E](@BeanProperty
                       @(Descriptor @field)(title = "Search")
                       var search : S,
                       rows: util.List[E],
                       size: Long) extends Table[E](rows, size) {

  def this() = this(null.asInstanceOf[S], null, 0L)

}
