package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.media.FileDiskUtils
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{Entity, PostLoad, PostPersist, PostRemove, PostUpdate, Transient}
import org.apache.commons.io.{FileUtils, IOUtils}
import org.hibernate.envers.Audited

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
@Audited
class File extends AbstractEntity {

  @Descriptor(title = "Type")
  @BeanProperty
  var `type` : String = uninitialized

  @Descriptor(title = "Subtype")
  @BeanProperty
  var subType : String = uninitialized

  @Descriptor(title = "Filename")
  @BeanProperty
  var name : String = uninitialized

  @Descriptor(title = "Data")
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
