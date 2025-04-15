package com.anjunar.technologyspeaks.security

import com.anjunar.technologyspeaks.control.{Role, User, UserFormResource}
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{createProxy, linkTo, methodOn}
import com.anjunar.technologyspeaks.jpa.Pair
import com.anjunar.scala.mapper.annotations.JsonSchema.State
import com.anjunar.scala.mapper.annotations.{Action, JsonSchema}
import com.anjunar.scala.schema.model.LinkType
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response

import java.util
import scala.compiletime.uninitialized


@ApplicationScoped
@Path("security/confirm") 
class ConfirmationFormResource {
  
  @Inject
  var mailService : MailService = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[ConfirmationFormSchema], state = State.ENTRYPOINT)
  @LinkDescription(value = "Bestätigung", linkType = LinkType.FORM)
  def create: Confirmation = new Confirmation

  @POST
  @Produces(Array("application/json"))
  @Consumes(Array("application/json"))
  @JsonSchema(value = classOf[ConfirmationReturnSchema], state = State.EXECUTE)
  @LinkDescription(value = "Bestätigung", linkType = LinkType.FORM)
  def confirm(@JsonSchema(value = classOf[ConfirmationFormSchema], state = State.EXECUTE) confirmation: Confirmation): User = {
    val current = User.current()
    
    val value = current
      .emails
      .stream()
      .flatMap(email => email.credentials.stream())
      .filter(token => token.oneTimeToken == confirmation.code)
      .findFirst()
    
    if (value.isPresent)
      val userRole = Role.query(Pair("name", "User"))
      val user = value.get()
      user.roles.clear()
      user.roles.add(userRole)
    else 
      throw new WebApplicationException(Response.Status.FORBIDDEN)
    
    current
  }

  @PUT
  @Produces(Array("application/json"))
  @JsonSchema(value = classOf[ConfirmationReturnSchema], state = State.EXECUTE)
  @LinkDescription(value = "Neuer Code", linkType = LinkType.ACTION)
  def reSend(@QueryParam("email") value : String): User = {
    val user = User.current()

    val tokens = user
      .emails
      .stream()
      .filter(email => email.value == value)
      .flatMap(email => email.credentials.stream())
      .filter(token => ! token.validated)
      .toList

    tokens.forEach(token => {
      token.generateOneTimeToken()
      
      val variables = new util.HashMap[String, AnyRef]
      variables.put("token", token)
      
      mailService.send(token.email.value, variables, "/templates/RegisterTemplate.html", "Technology Speaks")
    })  
    
    Response.ok().build()
    user
  }
}
