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
import jakarta.ws.rs.*

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
  var service : DocumentService = uninitialized

  @POST
  @Produces(Array("application/json"))
  @Consumes(Array("application/json"))
  @JsonSchema(classOf[DocumentTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Documents", linkType = LinkType.TABLE)
  def list(search : DocumentTableSearch): QueryTable[DocumentTableSearch, Document] = {

    val entities: util.List[Document] = if (Strings.isNullOrEmpty(search.text)) {
      Lists.newArrayList()
    } else {
      searchWithAI(search)
    }

    forLinks(classOf[QueryTable[DocumentTableSearch, Document]], (instance, link) => {
      linkTo(methodOn(classOf[DocumentFormResource]).create)
        .build(link.addLink)
    })

    entities.forEach(entity => {
      forLinks(entity, classOf[Document], (row, link) => {
        linkTo(methodOn(classOf[DocumentFormResource]).read(row.id))
          .build(link.addLink)

        val chunkSearch = new ChunkTableSearch
        chunkSearch.document = row.id

        linkTo(methodOn(classOf[ChunkTableResource]).list(chunkSearch))
          .build(link.addLink)

      })
    })

    new QueryTable[DocumentTableSearch, Document](new DocumentTableSearch(), entities, entities.size())
  }

  private def searchWithAI(search: DocumentTableSearch) = {
    val embeddings = service.createEmbeddings(search.text)
    val chunks = service.findTop5Embeddings(embeddings)

    val docStatsMap: Map[Document, DocStats] = chunks.asScala
      .groupBy(_.document)
      .view
      .mapValues { chunkList =>
        val count = chunkList.size
        val totalDist = chunkList.map(_.distance).sum
        DocStats(chunkList.head.document, count, totalDist)
      }.toMap

    def score(docStat: DocStats): Double =
      docStat.count / (1.0 + docStat.avgDistance)

    val entities: util.List[Document] = docStatsMap.values
      .toList
      .sortBy(ds => -score(ds))
      .map { stat =>
        stat.document.score = score(stat)
        stat.document
      }
      .asJava
    entities
  }
}