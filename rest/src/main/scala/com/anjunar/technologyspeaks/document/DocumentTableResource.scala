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
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*

import java.util
import java.util.UUID
import java.util.stream.Collectors
import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.CollectionHasAsScala

@Path("documents")
@ApplicationScoped
@Secured 
class DocumentTableResource extends SchemaBuilderContext {

  @Inject
  var service : DocumentService = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[DocumentTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "Documents", linkType = LinkType.TABLE)
  def list(@BeanParam search : DocumentSearch): QueryTable[DocumentSearch, Document] = {

    val embeddings = service.createEmbeddings(search.text)
    val chunks = service.find(embeddings)

    val docStatsMap = chunks.stream()
      .collect(Collectors.groupingBy(
        (chunk: Chunk) => chunk.document,
        Collectors.collectingAndThen(
          Collectors.toList(),
          (chunkList: util.List[Chunk]) => {
            val count = chunkList.size()
            val totalDist = chunkList.asScala.map(_.distance).sum
            DocStats(chunkList.get(0).document, count, totalDist)
          }
        )
      ))

    def score(docStat: DocStats): Double = {
      docStat.count / (1.0 + docStat.avgDistance)
    }

    val entities = docStatsMap.values().stream()
      .sorted((a, b) => java.lang.Double.compare(score(b), score(a)))
      .map(stat => {
        stat.document.score = score(stat)
        stat.document
      })
      .collect(Collectors.toList())


    forLinks(classOf[QueryTable[DocumentSearch, Document]], (instance, link) => {
      linkTo(methodOn(classOf[DocumentFormResource]).create)
        .build(link.addLink)
    })

    entities.forEach(entity => {
      forLinks(entity, classOf[Document], (row, link) => {
        linkTo(methodOn(classOf[DocumentFormResource]).read(row.id))
          .build(link.addLink)
      })
    })

    new QueryTable[DocumentSearch, Document](new DocumentSearch(), entities, entities.size())
  }
}