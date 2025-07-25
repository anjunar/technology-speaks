package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.configuration.jaxrs.ObjectMapperContextResolver
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.security.Secured
import com.anjunar.technologyspeaks.shared.editor.{ASTDiffUtil, Change}
import com.github.gumtreediff.actions.ChawatheScriptGenerator
import com.github.gumtreediff.matchers.Matchers
import com.typesafe.scalalogging.Logger
import jakarta.annotation.Resource
import jakarta.annotation.security.RolesAllowed
import jakarta.batch.runtime.BatchRuntime
import jakarta.enterprise.concurrent.ManagedExecutorService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.{MediaType, Response, StreamingOutput}

import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicBoolean
import java.util.{Properties, UUID}
import java.util.concurrent.{BlockingQueue, ConcurrentHashMap, LinkedBlockingQueue}
import scala.compiletime.uninitialized

@Path("documents/document")
@ApplicationScoped
@Secured
class DocumentFormResource extends SchemaBuilderContext {

  val log: Logger = Logger[DocumentFormResource]

  @Inject
  var aiService : DocumentAIService = uninitialized
  
  @Inject
  var service : DocumentService = uninitialized

  @Resource
  var executor: ManagedExecutorService  = uninitialized

  val batchSessions : ConcurrentHashMap[UUID, BlockingQueue[String]] = new ConcurrentHashMap[UUID, BlockingQueue[String]]()

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Create", linkType = LinkType.FORM)
  def create: Document = {

    forLinks(classOf[Document], (instance, link) => {
      linkTo(methodOn(classOf[DocumentFormResource]).save(instance))
        .withRel("submit")
        .build(link.addLink)
    })

    val Document = new Document

    Document.user = User.current()

    Document
  }

  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Read", linkType = LinkType.FORM)
  def read(@PathParam("id") id: UUID, @QueryParam("edit") edit : Boolean): Document = {
    val document = Document.find(id)

    forLinks(classOf[Document], (instance, link) => {
      linkTo(methodOn(classOf[DocumentFormResource]).read(instance.id, true))
        .build(link.addLink)
      linkTo(methodOn(classOf[DocumentFormResource]).save(instance))
        .withRel("submit")
        .build(link.addLink)
      linkTo(methodOn(classOf[DocumentFormResource]).delete(instance))
        .build(link.addLink)

      val chunkSearch = new ChunkSearch
      chunkSearch.document = document
      linkTo(methodOn(classOf[ChunkTableResource]).search(chunkSearch))
        .withRel("chunks")
        .build(link.addLink)

      val revisionSearch = new RevisionSearch
      revisionSearch.document = document
      linkTo(methodOn(classOf[RevisionsTableResource]).search(revisionSearch))
        .withRel("revisions")
        .build(link.addLink)

      linkTo(methodOn(classOf[DocumentTableResource]).list(null))
        .withRedirect
        .build(link.addLink)
    })
    
    document
  }

  @GET
  @Path("{id}/revisions/revision/{rev}/compare")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Compare", linkType = LinkType.FORM)
  def compareRevision(@PathParam("id") id: UUID, @PathParam("rev") revision: Int): Document = {
    val document = Document.find(id)
    val revDocument = Document.find(id, revision)

    val oldContext = ASTDiffUtil.buildTreeContext(revDocument.editor.json)
    val newContext = ASTDiffUtil.buildTreeContext(document.editor.json)

    val matcher = Matchers.getInstance().getMatcher.`match`(oldContext.getRoot, newContext.getRoot)

    val editScript = new ChawatheScriptGenerator().computeActions(matcher)

    val actions = editScript.asList()

    val changes = Change.extractChanges(actions)

    document.editor.changes.addAll(changes)

    document
  }

  @GET
  @Path("{id}/revisions/revision/{rev}/view")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "View", linkType = LinkType.FORM)
  def viewRevision(@PathParam("id") id: UUID, @PathParam("rev") revision: Int): Document = Document.find(id, revision)

  @POST
  @Consumes(Array("application/json", MediaType.MULTIPART_FORM_DATA))
  @JsonSchema(classOf[DocumentFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Save", linkType = LinkType.FORM)
  def save(@JsonSchema(classOf[DocumentFormSchema]) entity: Document): Response = {
    entity.user = User.current()

    entity.saveOrUpdate()
    
    createRedirectResponse
  }

  @DELETE
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Delete", linkType = LinkType.FORM)
  def delete(@JsonSchema(classOf[DocumentFormSchema]) entity: Document): Document = {
    entity.delete()

    forLinks(classOf[Document], (instance, link) => {
      linkTo(methodOn(classOf[DocumentTableResource]).list(null))
        .withRedirect
        .build(link.addLink)
    })

    entity
  }

  @GET
  @Path("/{id}/batch")
  @RolesAllowed(Array("User", "Administrator"))
  @Produces(Array(MediaType.SERVER_SENT_EVENTS))
  def progressStream(@PathParam("id") document: Document, @QueryParam("session") session : UUID): Response = {

    val cancelled = new AtomicBoolean(false)
    
    val service = aiService
    var queue = batchSessions.get(session)

    if (queue == null) {
      queue = new LinkedBlockingQueue[String]
      batchSessions.put(session, queue)

      executor.runAsync(() => {
        try {
          service.update(document.id, queue, cancelled)
        } catch {
          case e: Exception =>
            queue.offer(s"Error: ${e.getMessage}")
            queue.offer("!Done!")
        }
      })
    }

    val mapper = ObjectMapperContextResolver.objectMapper

    val stream: StreamingOutput = output => {
      try {
        var done = false
        while (!done && !cancelled.get()) {
          val msg = queue.take()
          val json = mapper.writeValueAsString(Map("text" -> msg))
          val sseMsg = s"data: $json\n\n"
          output.write(sseMsg.getBytes(StandardCharsets.UTF_8))
          output.flush()
          if (msg == "!Done!") done = true
        }
      } catch {
        case e: InterruptedException =>
          log.error(e.getMessage, e)
          cancelled.set(true)
      } finally {
        batchSessions.remove(session)
      }
    }

    Response.ok(stream).build()
  }
}