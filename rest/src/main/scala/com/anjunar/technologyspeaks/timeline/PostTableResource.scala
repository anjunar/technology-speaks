package com.anjunar.technologyspeaks.timeline

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.search.{RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.JPASearch
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericIdProvider, GenericNameProvider, GenericSortProvider}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, Table}
import com.anjunar.technologyspeaks.security.Secured
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.{BeanParam, GET, Path, Produces, QueryParam}

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Path("timeline/posts")
@ApplicationScoped
@Secured 
class PostTableResource extends SchemaBuilderContext {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[PostTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Timeline", linkType = LinkType.TABLE)
  def list(@BeanParam search: PostTableResource.Search): Table[Post] = {
    val context = jpaSearch.searchContext(search)
    val entities = jpaSearch.entities(search.index, search.limit, classOf[Post], context)
    val count = jpaSearch.count(classOf[Post], context)

    forLinks(classOf[Table[Post]], (instance, link) => {
      linkTo(methodOn(classOf[PostFormResource]).create)
        .build(link.addLink)
    })

    entities.forEach(entity => {
      forLinks(entity, classOf[Post], (row, link) => {
        linkTo(methodOn(classOf[PostFormResource]).read(row.id))
          .build(link.addLink)
      })
    })

    new Table[Post](entities, count)
  }
}

object PostTableResource {

  class Search extends AbstractSearch {

    @RestSort(classOf[GenericSortProvider[?]])
    @QueryParam("sort")
    @BeanProperty
    var sort: util.List[String] = uninitialized

    @RestPredicate(classOf[GenericIdProvider[?]])
    @QueryParam("id")
    @BeanProperty
    var id: UUID = uninitialized

  }
}
