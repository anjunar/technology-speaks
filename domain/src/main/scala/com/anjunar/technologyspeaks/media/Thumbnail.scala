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

  @Transient
  @BeanProperty
  @Descriptor(title = "Data")
  var data: Array[Byte] = uninitialized

  @PostLoad
  def postLoad(): Unit = {
    val file = FileDiskUtils.workingFile(id)
    data = IOUtils.toByteArray(file.toURI)
  }

  @PostPersist
  def postPersist(): Unit = {
    val file = FileDiskUtils.workingFile(id)
    FileUtils.writeByteArrayToFile(file, data)
  }

  @PostUpdate
  def postUpdate(): Unit = {
    val file = FileDiskUtils.workingFile(id)
    FileUtils.writeByteArrayToFile(file, data)
  }

  @PostRemove
  def postRemove(): Unit = {
    val file = FileDiskUtils.workingFile(id)
    file.delete()
  }
}


