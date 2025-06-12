package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.scala.mapper.file.{File, FileContext}
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.security.SecurityUser
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.technologyspeaks.shared.editor.{ASTDiffUtil, Change, Editor, EditorFile}
import com.anjunar.technologyspeaks.shared.hashtag.HashTag
import com.github.gumtreediff.actions.{ChawatheScriptGenerator, EditScriptGenerator, InsertDeleteChawatheScriptGenerator}
import com.github.gumtreediff.matchers.Matchers
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CollectionType
import org.hibernate.envers.{AuditReaderFactory, Audited, NotAudited}

import java.util
import java.util.Locale
import scala.beans.BeanProperty
import scala.collection.mutable
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

@Entity
@Audited
class Document extends AbstractEntity with OwnerProvider with FileContext {

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
  
  @Descriptor(title = "Language")
  @BeanProperty
  var language : Locale = uninitialized
  
  def files : util.List[File] = editor.files
  
  def create : File = new EditorFile

  override def owner: SecurityUser = user
  
  override def toString = s"Document($title, $description, $language)"
}

object Document extends RepositoryContext[Document](classOf[Document]) {

  def revisions(document: Document, index: Int, limit: Int): (Int, util.List[Revision]) = {
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
      val revision = new Revision
      revision.id = revDocument.id
      revision.revision = rev.intValue()
      revision.title = revDocument.title

      val oldContext = ASTDiffUtil.buildTreeContext(revDocument.editor.json)
      val newContext = ASTDiffUtil.buildTreeContext(document.editor.json)

      val matcher = Matchers.getInstance().getMatcher.`match`(oldContext.getRoot, newContext.getRoot)

      val editScript = new ChawatheScriptGenerator().computeActions(matcher)

      val actions = editScript.asList()

      val changes = Change.extractChanges(actions)
      
      revision.editor = document.editor
      revision.editor.changes.addAll(changes)
      
      revision
    }).toList)
  }

}
