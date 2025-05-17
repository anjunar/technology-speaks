package com.anjunar.technologyspeaks.document

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
import jakarta.ws.rs.*

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized


@Path("documents/document/chunks")
@ApplicationScoped
@Secured
class ChunkTableResource extends SchemaBuilderContext {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[ChunkTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Document Chunks", linkType = LinkType.TABLE)
  def list(@BeanParam search: ChunkTableSearch): QueryTable[ChunkTableSearch, Chunk] = {
    val context = jpaSearch.searchContext(search)
    val tuples = jpaSearch.entities(search.index, search.limit, classOf[Chunk], context)
    val entities = tuples.stream().map(tuple => tuple.get(0, classOf[Chunk])).toList
    val count = jpaSearch.count(classOf[Chunk], context)
    new QueryTable[ChunkTableSearch, Chunk](new ChunkTableSearch,entities, count)
  }
}