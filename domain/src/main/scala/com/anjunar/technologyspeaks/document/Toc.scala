package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jpa.{PostgresIndex, PostgresIndices, RepositoryContext}
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{CascadeType, Column, Entity, ManyToOne, OneToMany}
import org.hibernate.`type`.SqlTypes
import org.hibernate.annotations.JdbcTypeCode

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import java.util
import org.hibernate.annotations


@Entity
@PostgresIndices(Array(
  new PostgresIndex(name = "tag_idx_embedding", columnList = "embedding", using = "hnsw")
))
class Toc extends AbstractEntity {

  @Descriptor(title = "Parent")
  @ManyToOne
  @BeanProperty
  var parent : Toc = uninitialized

  @Descriptor(title = "Category")
  @BeanProperty
  var category : String = uninitialized

  @Descriptor(title = "Description")
  @BeanProperty
  var description : String = uninitialized

  @OneToMany(mappedBy = "parent", cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @Descriptor(title = "Children")
  @BeanProperty
  val children : util.List[Toc] = new util.ArrayList[Toc]()

  @Column
  @JdbcTypeCode(SqlTypes.VECTOR)
  @annotations.Array(length = 768)
  @BeanProperty
  var embedding: Array[Float] = uninitialized

}

object Toc extends RepositoryContext[Toc](classOf[Toc])