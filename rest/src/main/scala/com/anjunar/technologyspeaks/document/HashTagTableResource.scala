package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.JPASearch
import com.anjunar.technologyspeaks.jaxrs.types.Table
import com.anjunar.technologyspeaks.security.Secured
import com.anjunar.technologyspeaks.shared.hashtag.HashTag
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.{GET, Path, Produces}

import scala.compiletime.uninitialized

@ApplicationScoped
@Path("documents/document/hashtags")
@Secured
class HashTagTableResource extends SchemaBuilderContext {

  @Inject
  var jpaSearch: JPASearch = uninitialized

  @GET
  @Path("search")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[HashTagSearchSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "HashTags", linkType = LinkType.TABLE)
  def search(search : HashTagSearch) : HashTagSearch = {

    forLinks(classOf[HashTagSearch], (instance, link) => {
      linkTo(methodOn(classOf[HashTagTableResource]).list(search))
        .build(link.addLink)
    })

    new HashTagSearch
  }

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[HashTagTableSchema])
  @RolesAllowed(Array("User", "Administrator"))
  @LinkDescription(value = "HashTags", linkType = LinkType.TABLE)
  def list(search : HashTagSearch) : Table[HashTag] = {
    val context = jpaSearch.searchContext(search)
    val tuples = jpaSearch.entities(search.index, search.limit, classOf[HashTag], context)
    val entities = tuples.stream().map(tuple => tuple.get(0, classOf[HashTag])).toList
    val count = jpaSearch.count(classOf[HashTag], context)
    new Table[HashTag](entities, count)
  }

}
