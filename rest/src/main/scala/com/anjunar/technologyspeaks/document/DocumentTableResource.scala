package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{createProxy, linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.JPASearch
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericIdProvider, GenericNameProvider, GenericSortProvider}
import com.anjunar.technologyspeaks.jaxrs.search.{RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, QueryTable, Table}
import com.anjunar.technologyspeaks.security.Secured
import com.google.common.base.Strings
import com.google.common.collect.Lists
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.Tuple
import jakarta.ws.rs.*
import jakarta.ws.rs.core.{Context, HttpHeaders}

import java.util
import java.util.UUID
import java.util.stream.Collectors
import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

@Path("documents")
@ApplicationScoped
@Secured
class DocumentTableResource extends SchemaBuilderContext {

  @Inject
  var documentService: DocumentAIService = uninitialized

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Path("search")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentSearchSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Documents", linkType = LinkType.TABLE)
  def search(@BeanParam search: DocumentSearch): DocumentSearch = {

    forLinks(classOf[DocumentSearch], (instance, link) => {
      linkTo(methodOn(classOf[DocumentTableResource]).search(search))
        .withRel("submit")
        .build(link.addLink)
      linkTo(methodOn(classOf[DocumentTableResource]).list(search))
        .build(link.addLink)
    })

    new DocumentSearch
  }

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Documents", linkType = LinkType.TABLE)
  def list(@BeanParam search: DocumentSearch): Table[Tuple] = {

    val context = jpaSearch.searchContext(search)
    val entities = jpaSearch.entities(search.index, search.limit, classOf[Document], context)
    val count = jpaSearch.count(classOf[Document], context)

    forLinks(classOf[Table[Document]], (instance, link) => {
      linkTo(methodOn(classOf[DocumentFormResource]).create)
        .build(link.addLink)

      val search2 = new DocumentSearch
      search2.index = search.index
      search2.limit = search.limit
      search2.text = search.text
      linkTo(methodOn(classOf[DocumentTableResource]).search(search2))
        .build(link.addLink)

      if (search.index > 0) {
        val prev = new DocumentSearch
        prev.index = search.index - search.limit
        prev.text = search.text
        linkTo(methodOn(classOf[DocumentTableResource]).search(prev))
          .withRel("prev")
          .build(link.addLink)
      }

      if (search.index < (count - search.limit)) {
        val next = new DocumentSearch
        next.index = search.index + search.limit
        next.text = search.text
        linkTo(methodOn(classOf[DocumentTableResource]).search(next))
          .withRel("next")
          .build(link.addLink)
      }
    })

    entities.forEach(tuple => {
      val document = tuple.get(0, classOf[Document])
      forLinks(document, classOf[Document], (row, link) => {
        linkTo(methodOn(classOf[DocumentFormResource]).read(document.id, false))
          .build(link.addLink)
      })
    })

    new Table[Tuple](entities, count)
  }

}