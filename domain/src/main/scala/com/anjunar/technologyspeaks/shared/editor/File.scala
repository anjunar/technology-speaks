package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.technologyspeaks.media.FileDiskUtils
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{Entity, PostLoad, PostPersist, PostRemove, PostUpdate, Transient}
import org.apache.commons.io.{FileUtils, IOUtils}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class File extends AbstractEntity {

  @BeanProperty
  var `type` : String = uninitialized

  @BeanProperty
  var subType : String = uninitialized

  @BeanProperty
  var name : String = uninitialized

  @Transient
  @BeanProperty
  var data : Array[Byte] = uninitialized

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
