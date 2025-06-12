package com.anjunar.technologyspeaks.media

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.*
import org.apache.commons.io.{FileUtils, IOUtils}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
@Table(name = "media")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
class Thumbnail extends AbstractEntity {

  @PropertyDescriptor(title = "Name", naming = true)
  var name: String = uninitialized

  @PropertyDescriptor(title = "Type")
  var `type`: String = uninitialized

  @PropertyDescriptor(title = "Subtype")
  var subType: String = uninitialized

  @Lob
  @PropertyDescriptor(title = "Data")
  var data: Array[Byte] = uninitialized

}


