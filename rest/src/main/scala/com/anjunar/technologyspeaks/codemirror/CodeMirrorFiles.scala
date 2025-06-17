package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.google.common.base.Strings
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.{MediaType, Response}
import jakarta.ws.rs.{Consumes, GET, POST, Path, Produces}
import org.jboss.resteasy.annotations.jaxrs.PathParam

import java.util

@ApplicationScoped
@Path("codemirror/files")
class CodeMirrorFiles extends SchemaBuilderContext {

  @GET
  @Produces(Array("application/javascript", MediaType.TEXT_HTML))
  @Path("user/{user}/{file: .+}")
  def read(@PathParam("user") user : String, @PathParam("file") file : String) : Response = {

    if (file.startsWith("react") || file.startsWith("scheduler")) {
      val inputStream = getClass.getResourceAsStream("/javascript/" + file)
      return Response.ok(inputStream, "application/javascript").build()
    }

    val loaded = if (file.endsWith(".html") || file.endsWith(".css") || file.endsWith(".js.map")) {
      if (file.endsWith(".js.map")) {
        CodeMirrorFile.findByName("/" + file.replace(".js.map", ".ts"))
      } else {
        CodeMirrorFile.findByName("/" + file)
      }
    } else {
      if (file.endsWith(".ts") || file.endsWith(".tsx")) {
        CodeMirrorFile.findByName("/" + file)
      } else {
        CodeMirrorFile.findByName("/" + file + ".ts")
      }
    }

    if (loaded.name.endsWith(".ts") || loaded.name.endsWith(".tsx")) {
      if (file.endsWith(".js.map")) {
        Response.ok(loaded.sourceMap, "application/javascript")
          .header("Access-Control-Allow-Origin" , "*")
          .build()
      } else {
        if (file.endsWith(".ts") || file.endsWith(".tsx")) {
          Response.ok(loaded.content, "application/javascript")
            .header("Access-Control-Allow-Origin" , "*")
            .build()
        } else {
          Response.ok(loaded.transpiled, "application/javascript")
            .header("Access-Control-Allow-Origin" , "*")
            .build()
        }
      }
    } else {
      val protocol = httpHeaders.getHeaderString("x-forwarded-protocol")
      val host = httpHeaders.getHeaderString("x-forwarded-host")

      Response.ok(loaded.content, MediaType.TEXT_HTML)
        .header("Content-Security-Policy", s"frame-ancestors $protocol://$host")
        .header("Access-Control-Allow-Origin" , "*")
        .build()
    }
  }

  @GET
  @Produces(Array(MediaType.TEXT_HTML))
  @Path("user/{user}")
  def index(@PathParam("user") file: String): Response = {

    val loaded = CodeMirrorFile.findByName("/index.html")

    val protocol = httpHeaders.getHeaderString("x-forwarded-protocol")
    val host = httpHeaders.getHeaderString("x-forwarded-host")

    Response.ok(loaded.content, MediaType.TEXT_HTML)
        .header("Content-Security-Policy", s"default-src 'none'; script-src 'self'; style-src 'self'; img-src 'self' data:; connect-src 'self';frame-ancestors $protocol://$host")
        .build()
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
      if (!Strings.isNullOrEmpty(file.sourceMap)) {
        loaded.sourceMap = file.sourceMap
      }
    }
    Response.ok().build()
  }

  @GET
  @Produces(Array("application/json"))
  def files() : util.List[CodeMirrorFile] = {
    CodeMirrorFile.findAll()
      .stream()
      .filter(file => file.name.endsWith(".ts") || file.name.endsWith(".tsx") || file.name.endsWith(".html") || file.name.endsWith(".css"))
      .toList()
  }



}
