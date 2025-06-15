package com.anjunar.technologyspeaks

import com.typesafe.scalalogging.Logger
import jakarta.annotation.Resource
import jakarta.enterprise.concurrent.ManagedExecutorService
import jakarta.websocket.{CloseReason, OnClose, OnError, OnOpen, Session}

import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{BlockingQueue, ConcurrentHashMap, CopyOnWriteArraySet, ScheduledExecutorService, ScheduledFuture, TimeUnit}
import scala.compiletime.uninitialized

class AbstractEndpoint {
  
  val log : Logger = Logger[AbstractEndpoint]

  @Resource
  var executor: ManagedExecutorService = uninitialized

  @Resource(lookup = "java:comp/DefaultManagedScheduledExecutorService")
  var scheduler: ScheduledExecutorService = uninitialized

  val batchSessions: ConcurrentHashMap[Session, BlockingQueue[String]] = new ConcurrentHashMap[Session, BlockingQueue[String]]()

  val canceledSessions: ConcurrentHashMap[Session, AtomicBoolean] = new ConcurrentHashMap[Session, AtomicBoolean]()

  val schedulerFutures: ConcurrentHashMap[Session, ScheduledFuture[?]] = new ConcurrentHashMap[Session, ScheduledFuture[?]]()

  val sessions = new CopyOnWriteArraySet[Session]()
  
  @OnOpen
  def onOpen(session: Session): Unit = {
    sessions.add(session)
    log.info(s"Client connected: ${session.getId}")
    val future = scheduler.scheduleAtFixedRate(() => {
      if (session.isOpen) {
        try {
          session.getAsyncRemote.sendPing(ByteBuffer.wrap(Array()))
        } catch {
          case e: Exception => log.info(s"Ping failed for session ${session.getId}: ${e.getMessage}")
        }
      }
    }, 30, 30, TimeUnit.SECONDS)

    schedulerFutures.put(session, future)
  }
  
  @OnClose
  def onClose(session: Session, reason: CloseReason): Unit = {
    sessions.remove(session)
    batchSessions.remove(session)
    canceledSessions.get(session).set(true)
    canceledSessions.remove(session)
    val future = schedulerFutures.get(session)
    future.cancel(true)
    schedulerFutures.remove(session)
    log.info(s"Client disconnected: ${session.getId}, reason: ${reason.getReasonPhrase}")
  }

  @OnError
  def onError(session: Session, throwable: Throwable): Unit = {
    sessions.remove(session)
    batchSessions.remove(session)
    canceledSessions.get(session).set(true)
    canceledSessions.remove(session)
    val future = schedulerFutures.get(session)
    future.cancel(true)
    schedulerFutures.remove(session)
    log.info(s"Error on session ${session.getId}: ${throwable.getMessage}")
  }


}
