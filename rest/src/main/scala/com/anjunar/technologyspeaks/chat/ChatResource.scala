package com.anjunar.technologyspeaks.chat

import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.configuration.jaxrs.ObjectMapperContextResolver
import com.anjunar.technologyspeaks.document.Document
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.olama.AsyncOLlamaService
import com.anjunar.technologyspeaks.security.Secured
import com.typesafe.scalalogging.Logger
import jakarta.annotation.Resource
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.concurrent.ManagedExecutorService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.{GET, Path, PathParam, Produces}
import jakarta.ws.rs.core.{MediaType, Response, StreamingOutput}
import org.jboss.resteasy.annotations.jaxrs.QueryParam

import java.nio.charset.StandardCharsets
import java.util.UUID
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

  val batchSessions: ConcurrentHashMap[UUID, BlockingQueue[String]] = new ConcurrentHashMap[UUID, BlockingQueue[String]]()

  @GET
  @RolesAllowed(Array("User", "Administrator"))
  @Produces(Array(MediaType.SERVER_SENT_EVENTS))
  @LinkDescription(value = "Chat", linkType = LinkType.ACTION)
  def chat(@QueryParam("text") text : String, @QueryParam("session") session : UUID): Response = {
    val test = this.service

    var queue = batchSessions.get(session)
    if (queue == null) {
      queue = new LinkedBlockingQueue[String]
      batchSessions.put(session, queue)

      executor.runAsync(() => {
        try {
          test.chat(text, queue)
        } catch {
          case e: Throwable =>
            log.error(e.getMessage, e)
            queue.offer(s"Error: ${e.getMessage}")
            queue.offer("!Done!")
        }
      })
    }

    val mapper = ObjectMapperContextResolver.objectMapper

    val stream: StreamingOutput = output => {
      try {
        var done = false
        while (!done) {
          val msg = queue.take()
          val json = mapper.writeValueAsString(Map("text" -> msg))
          val sseMsg = s"data: $json\n\n"
          output.write(sseMsg.getBytes(StandardCharsets.UTF_8))
          output.flush()
          if (msg == "!Done!") done = true
        }
      } catch {
        case e: InterruptedException => log.error(e.getMessage, e)
      }
    }

    Response.ok(stream).build()
  }

}
