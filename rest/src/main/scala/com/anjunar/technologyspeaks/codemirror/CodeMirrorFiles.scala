package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.schema.builder.SchemaBuilderContext
import com.google.common.base.Strings
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.{Cookie, MediaType, NewCookie, Response}
import jakarta.ws.rs.{Consumes, DELETE, GET, PATCH, POST, Path, Produces}
import org.jboss.resteasy.annotations.jaxrs.{MatrixParam, PathParam, QueryParam}

import java.util

@ApplicationScoped
@Path("codemirror/{user}/files")
class CodeMirrorFiles extends SchemaBuilderContext {

  @GET
  @Produces(Array("application/javascript", MediaType.TEXT_HTML))
  @Path("file/{file: .+}")
  def read(@PathParam("user") user : String, @PathParam("file") file : String) : Response = {

    val loaded = file match {
      case arg : String if arg.startsWith("react") || arg.startsWith("scheduler") =>
        val inputStream = getClass.getResourceAsStream("/javascript/" + file)
        return Response.ok(inputStream, "application/javascript").build()
      case arg : String if arg.endsWith(".js.map") => AbstractCodeMirrorFile.findByName("/" + file.replace(".js.map", ".ts"))
      case arg : String if arg.endsWith(".js") => AbstractCodeMirrorFile.findByName("/" + file.replace(".js", ".ts"))
      case arg : String if arg.endsWith(".ts") || arg.endsWith("tsx") => AbstractCodeMirrorFile.findByName("/" + file)
      case _=> AbstractCodeMirrorFile.findByName("/" + file + ".ts")
    }

    loaded match {
      case css : CodeMirrorCSS =>
        Response.ok(css.content, "text/css")
          .build()
      case html : CodeMirrorHTML =>
        val protocol = httpHeaders.getHeaderString("x-forwarded-protocol")
        val host = httpHeaders.getHeaderString("x-forwarded-host")
        Response.ok(html.content, MediaType.TEXT_HTML)
          .header("Content-Security-Policy", s"default-src 'none'; script-src 'self'; style-src 'self'; img-src 'self' data:; connect-src 'self';frame-ancestors $protocol://$host")
          .build()
      case image : CodeMirrorImage =>
        Response.ok(image.content, image.contentType)
          .build()
      case ts : CodeMirrorTS =>
        file match {
          case arg : String if arg.endsWith(".js.map") =>
            Response.ok(ts.sourceMap, "application/javascript")
              .build()
          case arg : String if arg.endsWith("ts") || arg.endsWith("tsx") =>
            Response.ok(ts.content, "application/javascript")
              .build()
          case _ =>
            Response.ok(ts.transpiled, "application/javascript")
              .build()
        }
    }
  }

  @GET
  @Produces(Array(MediaType.TEXT_HTML))
  @Path("file")
  def index(@PathParam("user") user: String): Response = {

    val loaded = AbstractCodeMirrorFile.findByName("/index.html")

    val protocol = httpHeaders.getHeaderString("x-forwarded-protocol")
    val host = httpHeaders.getHeaderString("x-forwarded-host")

    Response.ok(if loaded == null then "" else loaded.content, MediaType.TEXT_HTML)
        .header("Content-Security-Policy", s"default-src 'none'; script-src 'self'; style-src 'self'; img-src 'self' data:; connect-src 'self';frame-ancestors $protocol://$host")
        .build()
  }

  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Path("file")
  def write(@PathParam("user") user : String, file : AbstractCodeMirrorFile) : Response = {
    val loaded = AbstractCodeMirrorFile.query(("name", file.name))
    if (loaded == null) {
      file.saveOrUpdate()
    } else {

      loaded match {
        case css : CodeMirrorCSS => css.content = file.content
        case html : CodeMirrorHTML => html.content = file.content
        case image : CodeMirrorImage =>
          file match {
            case newImage : CodeMirrorImage =>
              image.content = newImage.content
              image.contentType = newImage.contentType
          }
        case ts : CodeMirrorTS =>
          file match {
            case newTs : CodeMirrorTS =>
              if (!Strings.isNullOrEmpty(newTs.content)) {
                ts.content = newTs.content
              }
              if (! Strings.isNullOrEmpty(newTs.transpiled)) {
                ts.transpiled = newTs.transpiled
              }
              if (!Strings.isNullOrEmpty(newTs.sourceMap)) {
                ts.sourceMap = newTs.sourceMap
              }
          }
      }

    }
    Response.ok().build()
  }

  @DELETE
  @Produces(Array("application/javascript", MediaType.TEXT_HTML))
  @Path("file/{file: .+}")
  def delete(@PathParam("user") user: String, @PathParam("file") file: String): Response = {
    AbstractCodeMirrorFile.findByName("/" + file).delete()
    Response.ok().build()
  }

  @PATCH
  @Produces(Array("application/javascript", MediaType.TEXT_HTML))
  @Path("file/{file: .+}")
  def rename(@PathParam("user") user: String, @PathParam("file") file: String, @QueryParam("newName") newName : String): Response = {
    val codeMirrorFile = AbstractCodeMirrorFile.findByName("/" + file)
    codeMirrorFile.name = newName
    Response.ok().build()
  }

  @GET
  @Produces(Array("application/json"))
  def files(@PathParam("user") user : String) : util.List[AbstractCodeMirrorFile] = AbstractCodeMirrorFile.findAll()




}
