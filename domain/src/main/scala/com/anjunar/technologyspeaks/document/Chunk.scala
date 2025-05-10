package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{Column, Entity, Lob, ManyToOne, Transient}
import org.hibernate.`type`.SqlTypes
import org.hibernate.annotations.JdbcTypeCode

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import org.hibernate.annotations


@Entity
class Chunk extends AbstractEntity {

  @BeanProperty
  var title : String = uninitialized

  @Lob
  @BeanProperty
  var content : String = uninitialized

  @Transient
  @Descriptor(title = "Distance")
  @BeanProperty
  var distance: Double = uninitialized

  @Column
  @JdbcTypeCode(SqlTypes.VECTOR)
  @annotations.Array(length = 3072)
  @BeanProperty
  var embedding: Array[Float] = uninitialized

  @ManyToOne(optional = false)
  @BeanProperty
  var document: Document = uninitialized


}
