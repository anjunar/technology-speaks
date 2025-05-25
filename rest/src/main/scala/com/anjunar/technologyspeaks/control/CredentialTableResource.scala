package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.scala.universe.introspector
import com.anjunar.technologyspeaks.control.{Credential, User}
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.JPASearch
import com.anjunar.technologyspeaks.jaxrs.search.provider.*
import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider, RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, QueryTable, Table}
import com.anjunar.technologyspeaks.security.Secured
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import jakarta.ws.rs.*

import java.util
import java.util.UUID
import java.util.stream.Collectors
import scala.beans.BeanProperty
import scala.collection.mutable
import scala.compiletime.uninitialized

@Path("security/credentials")
@ApplicationScoped
@Secured
class CredentialTableResource extends SchemaBuilderContext {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Path("search")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[CredentialSearchSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Credentials", linkType = LinkType.TABLE)
  def search(@BeanParam search : CredentialSearch) : CredentialSearch = {

    forLinks(classOf[CredentialSearch], (instance, links) => {
      linkTo(methodOn(classOf[CredentialTableResource]).list(search))
        .build(links.addLink)
    })

    new CredentialSearch
  }

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[CredentialTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Credentials", linkType = LinkType.TABLE)
  def list(@BeanParam search: CredentialSearch): Table[Credential] = {
    val user = User.current()

    val context = jpaSearch.searchContext[CredentialSearch, Credential](search, (context: Context[CredentialSearch, Credential]) => {
      context.predicates.addOne(context.builder.equal(context.root.get("email").get("user"), user))
    })

    val tuples = jpaSearch.entities(search.index, search.limit, classOf[Credential], context)
    val entities = tuples.stream().map(tuple => tuple.get(0, classOf[Credential])).toList
    val count = jpaSearch.count(classOf[Credential], context)

    entities.forEach(entity => {
      forLinks(entity, classOf[Credential], (row, link) => {
        linkTo(methodOn(classOf[CredentialFormResource]).read(row.id))
          .build(link.addLink)
      })
    })

    new Table[Credential](entities, count)
  }


}