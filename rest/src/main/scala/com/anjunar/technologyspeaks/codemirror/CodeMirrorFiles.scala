package com.anjunar.technologyspeaks.codemirror

import com.google.common.base.Strings
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.{MediaType, Response}
import jakarta.ws.rs.{Consumes, GET, POST, Path, Produces}
import org.jboss.resteasy.annotations.jaxrs.PathParam

import java.util

@ApplicationScoped
@Path("codemirror/files")
class CodeMirrorFiles {

  @GET
  @Produces(Array("application/javascript"))
  @Path("{file}")
  def read(@PathParam("file") file : String) : String = {
    CodeMirrorFile.findByName("/" + file).transpiled
  }

  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  def write(file : CodeMirrorFile) : Response = {
    val loaded = CodeMirrorFile.query(("name", file.name))
    if (loaded == null) {
      file.saveOrUpdate()
    } else {
      if (! Strings.isNullOrEmpty(file.content)) {
        loaded.content = file.content
      }
      if (! Strings.isNullOrEmpty(file.transpiled)) {
        loaded.transpiled = file.transpiled
      }
    }
    Response.ok().build()
  }

  @GET
  @Produces(Array("application/json"))
  def files() : util.List[CodeMirrorFile] = {
    CodeMirrorFile.findAll()
      .stream()
      .filter(file => file.name.endsWith("ts") || file.name.endsWith("tsx"))
      .toList()
  }

}
