package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.UserTableSchema
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.Table
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.{BeanParam, GET, Path, Produces}

@ApplicationScoped
@Path("documents/document")
class RevisionsTableResource extends SchemaBuilderContext {

  @GET
  @Produces(Array("application/json"))
  @Path("{id}/revisions/search")
  @JsonSchema(classOf[RevisionSearchSchema])
  @LinkDescription(value = "Revisions", linkType = LinkType.TABLE)
  def search(@BeanParam search: RevisionSearch): RevisionSearch = {

    forLinks(classOf[RevisionSearch], (instance, link) => {
      linkTo(methodOn(classOf[RevisionsTableResource]).list(search))
        .build(link.addLink)
    })

    new RevisionSearch
  }


  @GET
  @Produces(Array("application/json"))
  @Path("{id}/revisions")
  @JsonSchema(classOf[DocumentTableSchema])
  @LinkDescription(value = "Revisions", linkType = LinkType.TABLE)
  def list(@BeanParam search: RevisionSearch): Table[Document] = {
    val (count, entities) = Document.revisions(search.document, search.index, search.limit)
    new Table[Document](entities, count)
  }

}
