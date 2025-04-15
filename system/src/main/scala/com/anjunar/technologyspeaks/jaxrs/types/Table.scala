package com.anjunar.technologyspeaks.jaxrs.types

import com.anjunar.scala.mapper.annotations.Descriptor

import java.util
import java.util.List
import scala.beans.BeanProperty


class Table[E](_rows: util.List[E],
               _size: Long) {

  @BeanProperty
  @Descriptor(title = "Reihen", widget = "table") 
  var rows: util.List[E] = _rows

  @BeanProperty
  @Descriptor(title = "Größe") 
  var size: Long = _size
  
  def this() = {
    this(null, 0L)
  }

}
