package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jpa.{PostgresIndex, PostgresIndices}
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{Column, Entity, Lob, ManyToOne, Transient}
import org.hibernate.`type`.SqlTypes
import org.hibernate.annotations.JdbcTypeCode

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import org.hibernate.annotations
import org.hibernate.envers.Audited


@Entity
@PostgresIndices(Array(
  new PostgresIndex(name = "chunk_idx_embedding", columnList = "embedding", using = "hnsw")
))
class Chunk extends AbstractEntity {

  @Descriptor(title = "Title")
  @BeanProperty
  var title : String = uninitialized

  @Descriptor(title = "Content")
  @Lob
  @BeanProperty
  var content : String = uninitialized

  @Transient
  @Descriptor(title = "Distance")
  @BeanProperty
  var distance: Double = uninitialized

  @Column
  @JdbcTypeCode(SqlTypes.VECTOR)
  @annotations.Array(length = 3840)
  @BeanProperty
  var embedding: Array[Float] = uninitialized

  @ManyToOne(optional = false)
  @BeanProperty
  var document: Document = uninitialized


}
