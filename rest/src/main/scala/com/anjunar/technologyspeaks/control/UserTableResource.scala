package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.{DoNotLoad, JsonSchema}
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.{JPASearch, JPASearchContext, JPASearchContextResult}
import com.anjunar.technologyspeaks.jaxrs.search.provider.*
import com.anjunar.technologyspeaks.jaxrs.search.{RestPredicate, RestSort, SearchBeanReader}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, QueryTable, Table}
import com.anjunar.technologyspeaks.security.Secured
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.{EntityManager, TupleElement}
import jakarta.persistence.criteria.*
import jakarta.persistence.Tuple
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
class UserTableResource extends SchemaBuilderContext {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Path("search")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[UserSearchSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Users", linkType = LinkType.TABLE)
  def search(@BeanParam search: UserSearch): UserSearch = {

    forLinks(classOf[UserSearch], (instance, link) => {
      linkTo(methodOn(classOf[UserTableResource]).list(search))
        .build(link.addLink)
    })

    new UserSearch
  }


  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[UserTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Users", linkType = LinkType.TABLE)
  def list(@BeanParam search: UserSearch): Table[Tuple] = {
    val context = jpaSearch.searchContext(search)
    val entities = jpaSearch.entities(search.index, search.limit, classOf[User], context)

    val count = jpaSearch.count(classOf[User], context)

    forLinks(classOf[Table[User]], (instance, link) => {
      linkTo(methodOn(classOf[UserFormResource]).create)
        .build(link.addLink)
    })

    entities.forEach(tuple => {
      val entity = tuple.get(0, classOf[User])
      forLinks(entity, classOf[User], (row, link) => {
        linkTo(methodOn(classOf[UserFormResource]).read(row.id))
          .build(link.addLink)
      })
    })

    new Table[Tuple](entities, count)
  }
}