package $package$

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.{Group, Role}
import com.anjunar.technologyspeaks.jaxrs.link.{LinkBody, LinkDescription}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.JPASearch
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericIdProvider, GenericManyToOneProvider, GenericNameProvider, GenericSortProvider}
import com.anjunar.technologyspeaks.jaxrs.search.{RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, QueryTable, Table}
import com.anjunar.technologyspeaks.security.Secured
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs._

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized


@Path("$path$")
@ApplicationScoped
@Secured
class $naming$TableResource extends SchemaBuilderContext {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Path("search")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[$naming$SearchSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "$naming$", linkType = LinkType.TABLE)
  def search(@BeanParam search: $naming$Search): $naming$Search = {

    forLinks(classOf[$naming$Search], (instance, link) => {
      linkTo(methodOn(classOf[$naming$TableResource]).list(search))
        .build(link.addLink)
    })

    new $naming$Search
  }


  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[$naming$TableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "$naming$", linkType = LinkType.TABLE)
  def list(@BeanParam search: $naming$Search): Table[$naming$] = {
    val context = jpaSearch.searchContext(search)
    val tuples = jpaSearch.entities(search.index, search.limit, classOf[$naming$], context)
    val entities = tuples.stream().map(tuple => tuple.get(0, classOf[$naming$])).toList
    val count = jpaSearch.count(classOf[$naming$], context)
    new Table[$naming$](entities, count)
  }
}
