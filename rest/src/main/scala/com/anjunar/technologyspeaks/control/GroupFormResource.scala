package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext, SchemaBuilderProvider}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.security.Secured
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.{ApplicationScoped, RequestScoped}
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response

import java.util.UUID
import scala.compiletime.uninitialized


@Path("control/groups/group")
@ApplicationScoped
@Secured
class GroupFormResource extends SchemaBuilderContext {

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[GroupFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Create", linkType = LinkType.FORM)
  def create: Group = {

    forLinks(classOf[Group], (instance, link) => {
      linkTo(methodOn(classOf[GroupFormResource]).save(instance))
        .withRel("submit")
        .build(link.addLink)
    })

    new Group
  }

  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[GroupFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Read", linkType = LinkType.FORM)
  def read(@PathParam("id") id: UUID): Group = {

    forLinks(classOf[Group], (instance, link) => {
      linkTo(methodOn(classOf[GroupFormResource]).save(instance))
        .withRel("submit")
        .build(link.addLink)
      linkTo(methodOn(classOf[GroupFormResource]).delete(instance))
        .build(link.addLink)
      linkTo(methodOn(classOf[GroupTableResource]).list(null))
        .withRedirect
        .build(link.addLink)
    })


    Group.find(id)
  }

  @POST
  @Consumes(Array("application/json"))
  @JsonSchema(classOf[GroupFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Save", linkType = LinkType.FORM)
  def save(@JsonSchema(classOf[GroupFormSchema]) entity: Group): Response = {

    val user = User.current()
    entity.user = user
    entity.saveOrUpdate()

    createRedirectResponse
  }

  @DELETE
  @Produces(Array("application/json"))
  @JsonSchema(classOf[GroupFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Delete", linkType = LinkType.FORM)
  def delete(@JsonSchema(classOf[GroupFormSchema]) entity: Group): Group = {
    entity.delete()

    forLinks(classOf[Group], (instance, link) => {
      linkTo(methodOn(classOf[GroupTableResource]).list(null))
        .withRedirect
        .build(link.addLink)
    })

    entity
  }
}
