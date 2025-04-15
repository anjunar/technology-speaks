package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.{JPASearch, JPASearchContext, JPASearchContextResult}
import com.anjunar.technologyspeaks.jaxrs.search.provider.*
import com.anjunar.technologyspeaks.jaxrs.search.{RestPredicate, RestSort, SearchBeanReader}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, Table}
import com.anjunar.technologyspeaks.security.Secured
import com.anjunar.scala.mapper.annotations.JsonSchema.State
import com.anjunar.scala.mapper.annotations.{DoNotLoad, JsonSchema}
import com.anjunar.scala.schema.model.LinkType
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*
import jakarta.ws.rs.{Path, *}

import java.net.SocketTimeoutException
import java.time.LocalDate
import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized


@Path("control/users")
@ApplicationScoped
@Secured
class UserTableResource {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[UserTableSchema], state = State.LIST)
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Benutzer", linkType = LinkType.TABLE)
  def list(@BeanParam search: UserTableResource.Search): Table[User] = {
    val context = jpaSearch.searchContext(search)
    val entities = jpaSearch.entities(search.index, search.limit, classOf[User], context)
    val count = jpaSearch.count(classOf[User], context)
    new Table[User](entities, count)
  }
}

object UserTableResource {
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
    @QueryParam("email")
    @BeanProperty
    private var email: String = uninitialized

    @RestPredicate(classOf[GenericNameProvider[?]])
    @QueryParam("firstName")
    @BeanProperty
    private var firstName: String = uninitialized

    @RestPredicate(classOf[GenericNameProvider[?]])
    @QueryParam("lastName")
    @BeanProperty
    private var lastName: String = uninitialized

    @RestPredicate(classOf[GenericDurationDateProvider[?]])
    @QueryParam("birthDate")
    @BeanProperty
    private var birthDate: LocalDate = uninitialized

    @RestPredicate(classOf[GenericManyToManyProvider[?]])
    @QueryParam("roles")
    @BeanProperty
    private var roles: util.Set[UUID] = uninitialized
  }
}
