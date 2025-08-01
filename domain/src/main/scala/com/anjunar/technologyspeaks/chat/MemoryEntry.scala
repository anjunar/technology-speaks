package com.anjunar.technologyspeaks.chat

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jpa.{PostgresIndex, PostgresIndices, RepositoryContext}
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{Basic, Column, Entity, Lob}
import org.hibernate.`type`.SqlTypes
import org.hibernate.annotations.JdbcTypeCode

import scala.compiletime.uninitialized
import org.hibernate.annotations

import java.util

@Entity
@PostgresIndices(Array(
  new PostgresIndex(name = "memory_entry_idx_embedding", columnList = "embedding", using = "hnsw")
))
class MemoryEntry extends AbstractEntity {

  @Lob
  @Column(columnDefinition = "text")
  @PropertyDescriptor(title = "Summary")
  @Basic
  var summary : String = uninitialized

  @Lob
  @Column(columnDefinition = "text")
  @PropertyDescriptor(title = "Question")
  @Basic
  var question: String = uninitialized

  @Lob
  @Column(columnDefinition = "text")
  @PropertyDescriptor(title = "Answer")
  @Basic
  var answer: String = uninitialized

  @Column
  @JdbcTypeCode(SqlTypes.VECTOR)
  @annotations.Array(length = 768)
  @Basic
  var embedding: Array[Float] = uninitialized

  def tokenSize: Int = question.split(" ").length + answer.split(" ").length

  override def toString = s"MemoryEntry($summary, $question, $answer)"
}

object MemoryEntry extends RepositoryContext[MemoryEntry](classOf[MemoryEntry]) {

  def apply(summary : String, question: String, answer: String, embedding: Array[Float]) : MemoryEntry = {
    val entry = new MemoryEntry()
    entry.summary = summary
    entry.question = question
    entry.answer = answer
    entry.embedding = embedding
    entry
  }

  def findLatest10() : util.List[MemoryEntry] = {
    entityManager.createQuery("SELECT e FROM MemoryEntry e ORDER BY e.created DESC ", classOf[MemoryEntry])
      .setMaxResults(10)
      .getResultList
      .reversed()
  }

  def findSimilar(vector : Array[Float]): util.List[MemoryEntry] = {
    entityManager.createQuery("SELECT e FROM MemoryEntry e WHERE function('cosine_distance', e.embedding, :vector) < 0.3 ORDER BY function('cosine_distance', e.embedding, :vector) ASC", classOf[MemoryEntry])
      .setParameter("vector", vector)
      .setMaxResults(10)
      .getResultList
  }

}