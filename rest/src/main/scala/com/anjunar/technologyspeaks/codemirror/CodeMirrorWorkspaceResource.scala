package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.technologyspeaks.control.User
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.{Consumes, GET, POST, Path, Produces}

@ApplicationScoped
@Path("codemirror/workspace")
class CodeMirrorWorkspaceResource {

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[CodeMirrorWorkspaceSchema])
  def read(): CodeMirrorWorkspace = {
    val user = User.current()

    val workspace = CodeMirrorWorkspace.findByUser(user)

    if (workspace == null) {
      val workspace = CodeMirrorWorkspace(user)
      workspace.saveOrUpdate()
      workspace
    } else {
      workspace
    }
  }

  @POST
  @Consumes(Array("application/json"))
  def save(@JsonSchema(classOf[CodeMirrorWorkspaceSchema]) entity : CodeMirrorWorkspace): Response = {
    entity.saveOrUpdate()

    Response.ok().build()
  }

}
