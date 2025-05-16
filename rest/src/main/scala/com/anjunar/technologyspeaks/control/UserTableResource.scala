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

  @POST
  @Produces(Array("application/json"))
  @Consumes(Array("application/json"))
  @JsonSchema(classOf[UserTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Benutzer", linkType = LinkType.TABLE)
  def list(search: UserTableSearch): QueryTable[UserTableSearch, User] = {
    val context = jpaSearch.searchContext(search)
    val tuples = jpaSearch.entities(search.index, search.limit, classOf[User], context)
    val entities = tuples.stream().map(tuple => {
      var user : User = null
      var score = 0.0
      tuple.getElements.forEach({
        case tupleElement : TupleElement[?] if tupleElement.getJavaType == classOf[User] => user = tuple.get(0, classOf[User])
        case tupleElement : TupleElement[?] if tupleElement.getAlias == "distance" => score = tuple.get(1, classOf[Double])
      })

      user.score = score
      user
    }).toList
    val count = jpaSearch.count(classOf[User], context)

    forLinks(classOf[Table[User]], (instance, link) => {
      linkTo(methodOn(classOf[UserFormResource]).create)
        .build(link.addLink)
    })

    entities.forEach(entity => {
      forLinks(entity, classOf[User], (row, link) => {
        linkTo(methodOn(classOf[UserFormResource]).read(row.id))
          .build(link.addLink)
      })
    })

    new QueryTable[UserTableSearch, User](new UserTableSearch,entities, count)
  }
}