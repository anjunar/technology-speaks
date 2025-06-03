package com.anjunar.technologyspeaks.shared.hashtag

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jpa.{PostgresIndex, PostgresIndices}
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{Column, Entity}
import org.hibernate.`type`.SqlTypes
import org.hibernate.annotations.JdbcTypeCode

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import org.hibernate.annotations


@Entity
@PostgresIndices(Array(
  new PostgresIndex(name = "hashtag_idx_value", columnList = "value", using = "GIN"),
  new PostgresIndex(name = "hashtag_idx_embedding", columnList = "embedding", using = "hnsw")
))
class HashTag extends AbstractEntity {

  @Descriptor(title = "HashTag", writeable = true, naming = true)
  @BeanProperty
  var value : String = uninitialized

  @Descriptor(title = "Description", writeable = true)
  @BeanProperty
  var description : String = uninitialized

  @Column
  @JdbcTypeCode(SqlTypes.VECTOR)
  @annotations.Array(length = 768)
  @BeanProperty
  var embedding: Array[Float] = uninitialized

}
