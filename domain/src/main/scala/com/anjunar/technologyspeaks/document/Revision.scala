package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.shared.editor.{Change, Editor}
import jakarta.persistence.Basic

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Revision {
  
  @PropertyDescriptor(title = "Id")
  @Basic
  var id : UUID = uninitialized

  @PropertyDescriptor(title = "Revision")
  @Basic
  var revision : Int = uninitialized

  @PropertyDescriptor(title = "Title")
  @Basic
  var title : String = uninitialized

  @PropertyDescriptor(title = "Editor")
  @Basic
  var editor : Editor = uninitialized
  
  override def toString = s"Revision($revision, $title)"
}
