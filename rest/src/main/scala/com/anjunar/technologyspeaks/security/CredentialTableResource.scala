package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.mapper.annotations.JsonSchema.State
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.Credential
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.search.{RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.JPASearch
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericDurationDateProvider, GenericIdProvider, GenericManyToManyProvider, GenericNameProvider, GenericSortProvider}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, Table}
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.{BeanParam, GET, Path, Produces, QueryParam}

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Path("security/credentials")
@ApplicationScoped
@Secured
class CredentialTableResource {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[CredentialTableSchema], state = State.LIST)
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Credentials", linkType = LinkType.TABLE)
  def list(@BeanParam search: CredentialTableResource.Search): Table[Credential] = {
    val context = jpaSearch.searchContext(search)
    val entities = jpaSearch.entities(search.index, search.limit, classOf[Credential], context)
    val count = jpaSearch.count(classOf[Credential], context)
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

