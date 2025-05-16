package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{SchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.scala.universe.introspector
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider, RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.JPASearch
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericIdProvider, GenericNameProvider, GenericSortProvider}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, QueryTable, Table}
import com.anjunar.technologyspeaks.security.Secured
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import jakarta.ws.rs.{BeanParam, Consumes, GET, POST, Path, Produces}
import org.jboss.resteasy.annotations.jaxrs.QueryParam

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import java.util
import java.util.UUID
import scala.collection.mutable

@ApplicationScoped
@Path("/control/groups")
@Secured
class GroupTableResource extends SchemaBuilderContext {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @POST
  @Produces(Array("application/json"))
  @Consumes(Array("application/json"))
  @JsonSchema(classOf[GroupTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Groups", linkType = LinkType.TABLE)
  def list(search: GroupTableSearch): QueryTable[GroupTableSearch, Group] = {
    val context = jpaSearch.searchContext(search, (context : Context[GroupTableSearch, Group]) => {
      context.builder.equal(context.root.get("user"), User.current())
    })
    val tuples = jpaSearch.entities(search.index, search.limit, classOf[Group], context)
    val entities = tuples.stream().map(tuple => tuple.get(0, classOf[Group])).toList
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

    new QueryTable[GroupTableSearch, Group](new GroupTableSearch, entities, count)
  }
}