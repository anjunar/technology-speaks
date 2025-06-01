package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.security.SecurityUser
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.technologyspeaks.shared.editor.Editor
import com.anjunar.technologyspeaks.shared.hashtag.HashTag
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CollectionType
import org.hibernate.envers.{AuditReaderFactory, Audited, NotAudited}

import java.util
import scala.beans.BeanProperty
import scala.collection.mutable
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

@Entity
@Audited
class Document extends AbstractEntity with OwnerProvider {

  @Size(min = 3, max = 80)
  @Descriptor(title = "Title")
  @BeanProperty
  var title: String = uninitialized

  @Lob
  @Descriptor(title = "Description")
  @NotAudited
  @BeanProperty
  var description: String = uninitialized

  @Descriptor(title = "User")
  @ManyToOne(optional = false)
  @BeanProperty
  @NotAudited
  var user: User = uninitialized

  @Descriptor(title = "Editor", widget = "editor")
  @OneToOne(optional = false, cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  var editor: Editor = uninitialized

  @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true, mappedBy = "document")
  @NotAudited
  @BeanProperty  
  val chunks: util.List[Chunk] = new util.ArrayList[Chunk]()

  @Descriptor(title = "HashTags", widget = "form-array")
  @ManyToMany
  @BeanProperty
  @NotAudited
  val hashTags : util.Set[HashTag] = new util.HashSet[HashTag]()
  
  @Descriptor(title = "Revision")
  @Transient
  @BeanProperty
  var revision : Number = -1

  override def owner: SecurityUser = user

  private def canEqual(other: Any): Boolean = other.isInstanceOf[Document]

  override def equals(other: Any): Boolean = other match {
    case that: Document =>
      super.equals(that) &&
        that.canEqual(this) &&
        revision == that.revision
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(super.hashCode(), revision)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

}

object Document extends RepositoryContext[Document](classOf[Document]) {

  def revisions(document: Document, index: Int, limit: Int): (Int, util.List[Document]) = {
    val auditReader = AuditReaderFactory.get(entityManager)
    val revisions = auditReader.getRevisions(classOf[Document], document.id)

    def paginateRevisions(revs: util.List[Number], page: Int, size: Int): util.List[Number] = {
      val from = page * size
      if (from >= revs.size) new util.ArrayList()
      else revs.asScala.slice(from, Math.min(from + size, revs.size())).asJava
    }

    val pageRevisions = paginateRevisions(revisions, index, limit)
    (revisions.size(), pageRevisions.stream.map(rev => {
      val revDocument = auditReader.find(classOf[Document], document.id, rev)
      revDocument.revision = rev
      revDocument
    }).toList)
  }

}
