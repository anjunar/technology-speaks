package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.mapper.annotations.JsonSchema
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.{Consumes, GET, POST, Path, PathParam, Produces}

@ApplicationScoped
@Path("codemirror/{user}")
class CodeMirrorTagResource {

  @GET
  @Produces(Array("application/json"))
  @Path("{tag}")
  @JsonSchema(classOf[CodeMirrorTagSchema])
  def read(@PathParam("tag") tag : CodeMirrorTag): CodeMirrorTag = tag

  @POST
  @Consumes(Array("application/json"))
  @Path("{tag}")
  def save(@JsonSchema(classOf[CodeMirrorTagSchema]) @PathParam("tag") tag : CodeMirrorTag): Response = {
    tag.saveOrUpdate()

    Response.ok.build()
  }


}
