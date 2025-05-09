package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.security.Secured
import com.anjunar.technologyspeaks.shared.editor.{ParagraphNode, RootNode, TextNode}
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*

import java.util.UUID
import scala.compiletime.uninitialized

@Path("documents/document")
@ApplicationScoped
@Secured
class DocumentFormResource extends SchemaBuilderContext {

  @Inject
  var service : DocumentService = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Create", linkType = LinkType.FORM)
  def create: Document = {

    forLinks(classOf[Document], (instance, link) => {
      linkTo(methodOn(classOf[DocumentFormResource]).save(instance))
        .build(link.addLink)
    })

    val Document = new Document

    val textNode = new TextNode()
    textNode.text = "Hello World"

    val paragraphNode = new ParagraphNode()
    paragraphNode.children.add(textNode)

    val rootNode = new RootNode()
    rootNode.children.add(paragraphNode)

    Document.user = User.current()
    Document.root = rootNode

    Document
  }

  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Read", linkType = LinkType.FORM)
  def read(@PathParam("id") id: UUID): Document = {

    forLinks(classOf[Document], (instance, link) => {
      linkTo(methodOn(classOf[DocumentFormResource]).update(instance))
        .build(link.addLink)
      linkTo(methodOn(classOf[DocumentFormResource]).delete(instance))
        .build(link.addLink)
      linkTo(methodOn(classOf[DocumentTableResource]).list(null))
        .withRedirect
        .build(link.addLink)
    })


    Document.find(id)
  }

  @POST
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Save", linkType = LinkType.FORM)
  def save(@JsonSchema(classOf[DocumentFormSchema]) entity: Document): Document = {
    entity.user = User.current()

    service.update(entity)

    entity.persist()

    forLinks(classOf[Document], (instance, link) => {
      linkTo(methodOn(classOf[DocumentFormResource]).update(instance))
        .build(link.addLink)
      linkTo(methodOn(classOf[DocumentFormResource]).delete(instance))
        .build(link.addLink)
      linkTo(methodOn(classOf[DocumentTableResource]).list(null))
        .withRedirect
        .build(link.addLink)
    })

    entity
  }

  @PUT
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Update", linkType = LinkType.FORM)
  def update(@JsonSchema(classOf[DocumentFormSchema]) entity: Document): Document = {
    entity.user = User.current()

    service.update(entity)

    entity.validate()

    forLinks(classOf[Document], (instance, link) => {
      linkTo(methodOn(classOf[DocumentFormResource]).delete(instance))
        .build(link.addLink)
      linkTo(methodOn(classOf[DocumentTableResource]).list(null))
        .withRedirect
        .build(link.addLink)
    })

    entity
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
}
