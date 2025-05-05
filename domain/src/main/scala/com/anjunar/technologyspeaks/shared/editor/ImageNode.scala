package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.technologyspeaks.media.FileDiskUtils
import jakarta.persistence.{Entity, PostLoad, PostPersist, PostRemove, PostUpdate, Transient}
import org.apache.commons.io.{FileUtils, IOUtils}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class ImageNode extends AbstractNode {

  @BeanProperty
  @Transient
  var src : Array[Byte] = uninitialized

  @BeanProperty
  var `type` : String = uninitialized

  @BeanProperty
  var subType : String = uninitialized

  @BeanProperty
  var aspectRatio : Double = uninitialized

  @BeanProperty
  var width : Double = uninitialized

  @BeanProperty
  var height : Double = uninitialized


  @PostLoad
  def postLoad(): Unit = {
    val file = FileDiskUtils.workingFile(id)
    src = IOUtils.toByteArray(file.toURI)
  }

  @PostPersist
  def postPersist(): Unit = {
    val file = FileDiskUtils.workingFile(id)
    FileUtils.writeByteArrayToFile(file, src)
  }

  @PostUpdate
  def postUpdate(): Unit = {
    val file = FileDiskUtils.workingFile(id)
    FileUtils.writeByteArrayToFile(file, src)
  }

  @PostRemove
  def postRemove(): Unit = {
    val file = FileDiskUtils.workingFile(id)
    file.delete()
  }

}
