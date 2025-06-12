package com.anjunar.technologyspeaks.timeline

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.security.Secured
import com.anjunar.technologyspeaks.shared.editor.Paragraph
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.{Consumes, DELETE, GET, POST, PUT, Path, PathParam, Produces}

import java.util.UUID

@Path("timeline/posts/post")
@ApplicationScoped
@Secured
class PostFormResource extends SchemaBuilderContext {

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[PostFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Create", linkType = LinkType.FORM)
  def create: Post = {

    forLinks(classOf[Post], (instance, link) => {
      linkTo(methodOn(classOf[PostFormResource]).save(instance))
        .withRel("submit")
        .build(link.addLink)
    })

    val post = new Post

    post.user = User.current()

    post
  }

  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[PostFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Read", linkType = LinkType.FORM)
  def read(@PathParam("id") id: UUID): Post = {

    forLinks(classOf[Post], (instance, link) => {
      linkTo(methodOn(classOf[PostFormResource]).save(instance))
        .withRel("submit")
        .build(link.addLink)
      linkTo(methodOn(classOf[PostFormResource]).delete(instance))
        .build(link.addLink)
      linkTo(methodOn(classOf[PostTableResource]).list(null))
        .withRedirect
        .build(link.addLink)
    })


    Post.find(id)
  }

  @POST
  @Consumes(Array("application/json"))
  @JsonSchema(classOf[PostFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Save", linkType = LinkType.FORM)
  def save(@JsonSchema(classOf[PostFormSchema]) entity: Post): Response = {
    entity.user = User.current()
    entity.saveOrUpdate()
    
    createRedirectResponse
  }

  @DELETE
  @Produces(Array("application/json"))
  @JsonSchema(classOf[PostFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Delete", linkType = LinkType.FORM)
  def delete(@JsonSchema(classOf[PostFormSchema]) entity: Post): Post = {
    entity.delete()

    forLinks(classOf[Post], (instance, link) => {
      linkTo(methodOn(classOf[PostTableResource]).list(null))
        .withRedirect
        .build(link.addLink)
    })

    entity
  }
}
