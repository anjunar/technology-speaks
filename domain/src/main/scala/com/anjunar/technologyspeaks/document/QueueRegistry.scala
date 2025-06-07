package com.anjunar.technologyspeaks.document

import jakarta.enterprise.context.ApplicationScoped

import java.util.UUID
import java.util.concurrent.*


@ApplicationScoped
class QueueRegistry {

  final private val queues = new ConcurrentHashMap[UUID, BlockingQueue[String]]

  def getOrCreateQueue(documentId: UUID): BlockingQueue[String] = queues.computeIfAbsent(documentId, (id: UUID) => new LinkedBlockingQueue[String])

  def getQueue(documentId: UUID): BlockingQueue[String] = queues.get(documentId)

  def removeQueue(documentId: UUID): Unit = {
    queues.remove(documentId)
  }

}