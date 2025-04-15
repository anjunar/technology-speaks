package com.anjunar.scala.schema.model

import com.anjunar.scala.mapper.annotations.IgnoreFilter

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@IgnoreFilter
class CollectionDescriptor extends NodeDescriptor {
  
  @BeanProperty
  var items : NodeDescriptor = uninitialized
  
}
