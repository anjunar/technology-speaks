package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.Role
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.JPASearch
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericIdProvider, GenericNameProvider, GenericSortProvider}
import com.anjunar.technologyspeaks.jaxrs.search.{RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, Table}
import com.anjunar.technologyspeaks.security.Secured
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized


@Path("control/roles")
@ApplicationScoped
@Secured class RoleTableResource extends SchemaBuilderContext {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[RoleTableSchema])
  @RolesAllowed(Array("Guest", "User", "Administrator"))
  @LinkDescription(value = "Rollen", linkType = LinkType.TABLE)
  def list(@BeanParam search: RoleTableResource.Search): Table[Role] = {
    val context = jpaSearch.searchContext(search)
    val entities = jpaSearch.entities(search.index, search.limit, classOf[Role], context)
    val count = jpaSearch.count(classOf[Role], context)

    forLinks(classOf[Table[Role]], (instance, link) => {
      linkTo(methodOn(classOf[RoleFormResource]).create)
        .build(link.addLink)
    })

    entities.forEach(entity => {
      forLinks(entity, classOf[Role], (row, link) => {
        linkTo(methodOn(classOf[RoleFormResource]).read(row.id))
          .build(link.addLink)
      })
    })

    new Table[Role](entities, count)
  }
}

object RoleTableResource {

  class Search extends AbstractSearch {

    @RestSort(classOf[GenericSortProvider[?]])
    @QueryParam("sort")
    @BeanProperty
    private var sort: util.List[String] = uninitialized

    @RestPredicate(classOf[GenericIdProvider[?]])
    @QueryParam("id")
    @BeanProperty
    private var id: UUID = uninitialized

    @RestPredicate(classOf[GenericNameProvider[?]])
    @QueryParam("name")
    @BeanProperty
    private var name: String = uninitialized

    @RestPredicate(classOf[GenericNameProvider[?]])
    @QueryParam("description")
    @BeanProperty
    private var description: String = uninitialized

  }
}
