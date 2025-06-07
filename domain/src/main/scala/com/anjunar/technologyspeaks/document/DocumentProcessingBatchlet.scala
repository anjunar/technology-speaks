package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.document.DocumentService
import com.typesafe.scalalogging.Logger
import jakarta.batch.api.AbstractBatchlet
import jakarta.batch.runtime.context.JobContext
import jakarta.inject.{Inject, Named}
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional

import java.util.UUID
import scala.compiletime.uninitialized


@Named("updateDocumentJob")
class DocumentProcessingBatchlet extends AbstractBatchlet {

  val logger: Logger = Logger[DocumentProcessingBatchlet]

  @Inject
  var jobContext: JobContext = uninitialized

  @Inject
  var documentService: DocumentService = uninitialized

  @Inject
  var entityManager: EntityManager = uninitialized

  @Inject
  var queueRegistry: QueueRegistry = uninitialized

  @throws[Exception]
  @Transactional  
  def process: String = {
    val documentId = UUID.fromString(jobContext.getProperties.getProperty("documentId"))
    val sessionId = UUID.fromString(jobContext.getProperties.getProperty("sessionId"))

    val document = entityManager.find(classOf[Document], documentId)
    if (document == null)
      throw new IllegalArgumentException("Not found")

    val queue = queueRegistry.getQueue(sessionId)
    if (queue == null) {
      throw new IllegalStateException("No queue found for sessionId: " + sessionId)
    }

    queue.put("Processing document ID: " + documentId + "\n")

    documentService.update(document, queue)
    queue.put("DONE")
    "COMPLETED"
  }
}