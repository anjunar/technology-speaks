package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.scala.universe.introspector
import com.anjunar.technologyspeaks.control.{Credential, User}
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.JPASearch
import com.anjunar.technologyspeaks.jaxrs.search.provider.*
import com.anjunar.technologyspeaks.jaxrs.search.{PredicateProvider, RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, Table}
import com.anjunar.technologyspeaks.security.CredentialTableResource.Search
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import jakarta.ws.rs.*

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Path("security/credentials")
@ApplicationScoped
@Secured
class CredentialTableResource extends SchemaBuilderContext {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[CredentialTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Credentials", linkType = LinkType.TABLE)
  def list(@BeanParam search: Search): Table[Credential] = {
    val user = User.current()

    val context = jpaSearch.searchContext[Search, Credential](search, (value: Search, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[Credential], query: CriteriaQuery[?], property: introspector.BeanProperty, name: String) => {
      builder.equal(root.get("email").get("user"), user)
    })

    val entities = jpaSearch.entities(search.index, search.limit, classOf[Credential], context)
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

object CredentialTableResource {
  class Search extends AbstractSearch {
    @RestSort(classOf[GenericSortProvider[?]])
    @QueryParam("sort")
    @BeanProperty
    private var sort: util.List[String] = uninitialized

    @RestPredicate(classOf[GenericNameProvider[?]])
    @QueryParam("displayName")
    @BeanProperty
    private var displayName: String = uninitialized

    @RestPredicate(classOf[GenericManyToManyProvider[?]])
    @QueryParam("roles")
    @BeanProperty
    private var roles: util.Set[UUID] = uninitialized
  }
}

