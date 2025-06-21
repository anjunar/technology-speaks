package com.anjunar.technologyspeaks.codemirror

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.{Consumes, GET, POST, Path, Produces}

@ApplicationScoped
@Path("codemirror/workspace")
class CodeMirrorWorkspaceResource {

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[CodeMirrorWorkspaceSchema])
  @LinkDescription(value = "Codemirror", linkType = LinkType.FORM)  
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
