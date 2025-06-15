package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.AbstractEndpoint
import jakarta.inject.Inject
import jakarta.websocket.{OnMessage, OnOpen, Session}
import jakarta.websocket.server.ServerEndpoint

import java.nio.ByteBuffer
import java.util.UUID
import java.util.concurrent.{LinkedBlockingQueue, TimeUnit}
import java.util.concurrent.atomic.AtomicBoolean
import scala.compiletime.uninitialized

@ServerEndpoint("/ws/document")
class DocumentFormEndpoint extends AbstractEndpoint {

  @Inject
  var service : DocumentAIService = uninitialized

  @OnMessage
  def onMessage(message: String, session: Session): Unit = {
    log.info(s"Received message from ${session.getId}: $message")

    var queue = batchSessions.get(session)
    val cancelled = canceledSessions.computeIfAbsent(session, _ => new AtomicBoolean(false))

    if (queue == null) {
      queue = new LinkedBlockingQueue[String]
      batchSessions.put(session, queue)

      executor.runAsync(() => {
        try {
          service.update(UUID.fromString(message), queue, cancelled)
        } catch {
          case e: Throwable =>
            log.error(e.getMessage, e)
            queue.offer(s"Error: ${e.getMessage}")
            queue.offer("!Done!")
        }
      })
    }

    executor.runAsync(() => {
      var done = false
      while (!done && !cancelled.get()) {
        val msg = queue.take()
        session.getAsyncRemote.sendText(msg)
        if (msg == "!Done!") done = true
      }
    })
  }


}
