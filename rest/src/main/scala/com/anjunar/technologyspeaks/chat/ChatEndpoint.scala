package com.anjunar.technologyspeaks.chat

import com.anjunar.technologyspeaks.AbstractEndpoint
import com.typesafe.scalalogging.Logger
import jakarta.inject.Inject
import jakarta.websocket.server.ServerEndpoint
import jakarta.websocket.{OnMessage, OnOpen, Session}

import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{LinkedBlockingQueue, TimeUnit}
import scala.compiletime.uninitialized

@ServerEndpoint("/ws/chat")
class ChatEndpoint extends AbstractEndpoint {

  override val log: Logger = Logger[ChatEndpoint]

  @Inject
  var service: ChatService = uninitialized

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
          service.chat(message, queue, cancelled)
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