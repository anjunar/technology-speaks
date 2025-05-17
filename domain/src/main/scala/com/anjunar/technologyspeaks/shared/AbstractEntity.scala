package com.anjunar.technologyspeaks.shared

import com.anjunar.technologyspeaks.jpa.EntityContext
import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.types.IdProvider
import jakarta.persistence.*

import java.time.LocalDateTime
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@MappedSuperclass
abstract class AbstractEntity extends EntityContext with IdProvider {

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @BeanProperty
  @Descriptor(title = "Id", id = true)
  val id: UUID = UUID.randomUUID()

  @Version
  @BeanProperty
  var version: Int = uninitialized

  @BeanProperty
  var created: LocalDateTime = uninitialized

  @BeanProperty
  var modified: LocalDateTime = uninitialized
  

  @PrePersist
  def onPersist(): Unit = {
    created = LocalDateTime.now()
    modified = LocalDateTime.now()
  }

  @PreUpdate
  def onMerge(): Unit = {
    modified = LocalDateTime.now()
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: AbstractEntity => this.id == that.id
    case _ => false
  }

  override def hashCode(): Int = id.hashCode()
}


