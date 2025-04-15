package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.mapper.annotations.Descriptor
import jakarta.persistence.{Embeddable, Lob}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized


@Embeddable
class Editor {

  @Lob
  @BeanProperty
  @Descriptor(title = "HTML")
  var html: String = uninitialized

  @Lob
  @BeanProperty
  @Descriptor(title = "Text")
  var text: String = uninitialized

}
