package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{SchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.scala.universe.introspector
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.search.{PredicateProvider, RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.JPASearch
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericIdProvider, GenericNameProvider, GenericSortProvider}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, Table}
import com.anjunar.technologyspeaks.security.Secured
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import jakarta.ws.rs.{BeanParam, GET, Path, Produces}
import org.jboss.resteasy.annotations.jaxrs.QueryParam

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import java.util
import java.util.UUID

@ApplicationScoped
@Path("/control/groups")
@Secured
class GroupTableResource extends SchemaBuilderContext {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[GroupTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Groups", linkType = LinkType.TABLE)
  def list(@BeanParam search: GroupTableResource.Search): Table[Group] = {
    val context = jpaSearch.searchContext(search, (value: GroupTableResource.Search, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[Group], query: CriteriaQuery[_], property: introspector.BeanProperty, name: String) => {
      builder.equal(root.get("user"), User.current())
    })
    val entities = jpaSearch.entities(search.index, search.limit, classOf[Group], context)
    val count = jpaSearch.count(classOf[Group], context)

    forLinks(classOf[Table[Group]], (instance, link) => {
      linkTo(methodOn(classOf[GroupFormResource]).create)
        .build(link.addLink)
    })

    entities.forEach(entity => {
      forLinks(entity, classOf[Group], (row, link) => {
        linkTo(methodOn(classOf[GroupFormResource]).read(row.id))
          .build(link.addLink)
      })
    })

    new Table[Group](entities, count)
  }
}

object GroupTableResource {

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
