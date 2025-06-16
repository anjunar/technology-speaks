package com.anjunar.technologyspeaks.chat

import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.configuration.jaxrs.ObjectMapperContextResolver
import com.anjunar.technologyspeaks.document.Document
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.security.Secured
import com.typesafe.scalalogging.Logger
import jakarta.annotation.Resource
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.concurrent.ManagedExecutorService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.container.{AsyncResponse, Suspended}
import jakarta.ws.rs.{GET, Path, PathParam, Produces}
import jakarta.ws.rs.core.{Context, MediaType, Response, StreamingOutput}
import jakarta.ws.rs.sse.{Sse, SseBroadcaster, SseEventSink}
import org.jboss.resteasy.annotations.jaxrs.QueryParam

import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{BlockingQueue, ConcurrentHashMap, LinkedBlockingQueue}
import scala.compiletime.uninitialized

@ApplicationScoped
@Path("chat")
@Secured
class ChatResource {

  val log: Logger = Logger[ChatResource]

  @Inject
  var service : ChatService = uninitialized

  @Resource
  var executor: ManagedExecutorService = uninitialized

  val broadcasters = new ConcurrentHashMap[UUID, SseBroadcaster]()

  val batchSessions: ConcurrentHashMap[UUID, BlockingQueue[String]] = new ConcurrentHashMap[UUID, BlockingQueue[String]]()

  @GET
  @RolesAllowed(Array("User", "Administrator"))
  @Produces(Array(MediaType.SERVER_SENT_EVENTS))
  @LinkDescription(value = "Chat", linkType = LinkType.ACTION)
  def chat(@QueryParam("text") text : String,
           @QueryParam("session") session : UUID,
           @Suspended asyncResponse: AsyncResponse,
           @Context sink: SseEventSink,
           @Context sse: Sse): Unit = {
    val test = this.service

    val cancelled = new AtomicBoolean(false)

    val broadcaster = broadcasters.computeIfAbsent(session, _ => sse.newBroadcaster())

    broadcaster.onClose((event) => {
      log.info("SSE sink closed, cleanup for session " + session)
      cancelled.set(true)
      batchSessions.remove(session)
      broadcasters.remove(session)
      try sink.close() catch {
        case e: Exception => log.warn("Error closing sink", e)
      }
    })

    broadcaster.register(sink)

    var queue = batchSessions.get(session)
    if (queue == null) {
      queue = new LinkedBlockingQueue[String]
      batchSessions.put(session, queue)

      executor.runAsync(() => {
        try {
          test.chat(text, queue, cancelled)
        } catch {
          case e: Throwable =>
            log.error(e.getMessage, e)
            queue.offer(s"Error: ${e.getMessage}")
            queue.offer("!Done!")
        }
      })
    }

    val mapper = ObjectMapperContextResolver.objectMapper

    executor.execute(() => {
      try {
        var done = false
        while (!done && !sink.isClosed) {
          val msg = queue.take()
          val json = mapper.writeValueAsString(Map("text" -> msg))
          val event = sse.newEventBuilder()
            .data(classOf[String], json)
            .build()

          broadcaster.broadcast(event)

          if (msg == "!Done!") done = true
        }
      } catch {
        case e: InterruptedException => log.warn("SSE thread interrupted")
        case e: Throwable => log.error("Unexpected error", e)
      } finally {
        log.info(s"Cleaning up SSE for session $session")
        cancelled.set(true)
        batchSessions.remove(session)
        broadcasters.remove(session)
        try sink.close() catch {
          case e: Exception => log.warn("Error closing SseEventSink", e)
        }
        asyncResponse.resume(Response.ok().build())
      }
    })
  }

}
