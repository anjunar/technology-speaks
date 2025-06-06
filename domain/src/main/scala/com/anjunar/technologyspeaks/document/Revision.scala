package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.shared.editor.{Change, Editor}

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Revision {
  
  @Descriptor(title = "Id")
  @BeanProperty
  var id : UUID = uninitialized

  @Descriptor(title = "Revision")
  @BeanProperty
  var revision : Int = uninitialized

  @Descriptor(title = "Title")
  @BeanProperty
  var title : String = uninitialized

  @Descriptor(title = "Editor")
  @BeanProperty
  var editor : Editor = uninitialized
  
  override def toString = s"Revision($revision, $title)"
}
