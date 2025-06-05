package com.anjunar.technologyspeaks.media

import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.scala.mapper.annotations.Descriptor
import jakarta.persistence.*
import org.apache.commons.io.{FileUtils, IOUtils}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
@Table(name = "media")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
class Thumbnail extends AbstractEntity {

  @BeanProperty
  @Descriptor(title = "Name", naming = true)
  var name: String = uninitialized

  @BeanProperty
  @Descriptor(title = "Type")
  var `type`: String = uninitialized

  @BeanProperty
  @Descriptor(title = "Subtype")
  var subType: String = uninitialized

  @Lob
  @BeanProperty
  @Descriptor(title = "Data")
  var data: Array[Byte] = uninitialized

}


