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

import java.util.UUID
import scala.compiletime.uninitialized


@Path("control/roles/role")
@ApplicationScoped
@Secured
class RoleFormResource extends SchemaBuilderContext {

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[RoleFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Create", linkType = LinkType.FORM)
  def create: Role = {

    provider.builder
      .forType(classOf[Role], (builder : EntitySchemaBuilder[Role]) => builder
        .withLinks((instance, link) => {
          linkTo(methodOn(classOf[RoleFormResource]).save(instance))
            .build(link.addLink)
        })
      )

    new Role
  }

  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[RoleFormSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Read", linkType = LinkType.FORM)
  def read(@PathParam("id") id: UUID): Role = {

    provider.builder
      .forType(classOf[Role], (builder: EntitySchemaBuilder[Role]) => builder
        .withLinks((instance, link) => {
          linkTo(methodOn(classOf[RoleFormResource]).update(instance))
            .build(link.addLink)
          linkTo(methodOn(classOf[RoleFormResource]).delete(instance))
            .build(link.addLink)
          linkTo(methodOn(classOf[RoleTableResource]).list(null))
            .withRedirect
            .build(link.addLink)
        })
      )


    Role.find(id)
  }

  @POST
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(classOf[RoleFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Save", linkType = LinkType.FORM)
  def save(@JsonSchema(classOf[RoleFormSchema]) entity: Role): Role = {
    entity.validate()
    entity.persist()

    provider.builder
      .forType(classOf[Role], (builder: EntitySchemaBuilder[Role]) => builder
        .withLinks((instance, link) => {
          linkTo(methodOn(classOf[RoleFormResource]).update(instance))
            .build(link.addLink)
          linkTo(methodOn(classOf[RoleFormResource]).delete(instance))
            .build(link.addLink)
          linkTo(methodOn(classOf[RoleTableResource]).list(null))
            .withRedirect
            .build(link.addLink)
        })
      )


    entity
  }

  @PUT
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @JsonSchema(classOf[RoleFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Update", linkType = LinkType.FORM)
  def update(@JsonSchema(classOf[RoleFormSchema]) entity: Role): Role = {
    entity.validate()

    provider.builder
      .forType(classOf[Role], (builder: EntitySchemaBuilder[Role]) => builder
        .withLinks((instance, link) => {
          linkTo(methodOn(classOf[RoleFormResource]).delete(instance))
            .build(link.addLink)
          linkTo(methodOn(classOf[RoleTableResource]).list(null))
            .withRedirect
            .build(link.addLink)
        })
      )

    entity
  }

  @DELETE
  @Produces(Array("application/json"))
  @JsonSchema(classOf[RoleFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Delete", linkType = LinkType.FORM)
  def delete(@JsonSchema(classOf[RoleFormSchema]) entity: Role): Role = {
    entity.delete()

    provider.builder
      .forType(classOf[Role], (builder: EntitySchemaBuilder[Role]) => builder
        .withLinks((instance, link) => {
          linkTo(methodOn(classOf[RoleTableResource]).list(null))
            .withRedirect
            .build(link.addLink)
        })
      )

    entity
  }
}
