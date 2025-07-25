package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.technologyspeaks.jaxrs.types.Table
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.{GET, Path, Produces}

@ApplicationScoped
@Path("codemirror/{user}")
class CodeMirrorTagsResource {

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[CodeMirrorTagsSchema])  
  def list(): Table[CodeMirrorTag] = {

    val entities = CodeMirrorTag.findAll()
    
    Table(entities, entities.size())
  }

}
