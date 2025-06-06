package com.anjunar.technologyspeaks.shared.i18n

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


@Path("/shared/i18ns")
@ApplicationScoped
@Secured
class I18nTableResource extends SchemaBuilderContext {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Path("search")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[I18nSearchSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "I18n", linkType = LinkType.TABLE)
  def search(@BeanParam search: I18nSearch): I18nSearch = {

    forLinks(classOf[I18nSearch], (instance, link) => {
      linkTo(methodOn(classOf[I18nTableResource]).list(search))
        .build(link.addLink)
    })

    new I18nSearch
  }


  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[I18nTableSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "I18n", linkType = LinkType.TABLE)
  def list(@BeanParam search: I18nSearch): Table[I18n] = {
    val context = jpaSearch.searchContext(search)
    val tuples = jpaSearch.entities(search.index, search.limit, classOf[I18n], context)
    val entities = tuples.stream().map(tuple => tuple.get(0, classOf[I18n])).toList
    val count = jpaSearch.count(classOf[I18n], context)

    entities.forEach(entity => {
      forLinks(entity, classOf[I18n], (row, link) => {
        linkTo(methodOn(classOf[I18nFormResource]).read(row.id))
          .build(link.addLink)
      })
    })

    new Table[I18n](entities, count)
  }
}
