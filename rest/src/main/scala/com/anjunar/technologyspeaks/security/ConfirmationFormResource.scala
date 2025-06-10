package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.{CredentialWebAuthn, Role, User, UserFormResource}
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{createProxy, linkTo, methodOn}
import com.anjunar.technologyspeaks.jpa.Pair
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response

import java.util
import scala.compiletime.uninitialized


@ApplicationScoped
@Path("security/confirm") 
class ConfirmationFormResource extends SchemaBuilderContext {
  
  @Inject
  var mailService : MailService = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[ConfirmationFormSchema])
  @LinkDescription(value = "Confirmation", linkType = LinkType.FORM)
  def create: Confirmation = {

  forLinks(classOf[Confirmation], (instance, link) => {
    linkTo(methodOn(classOf[ConfirmationFormResource]).confirm(null))
      .build(link.addLink)

    User.current()
      .emails
      .stream()
      .flatMap(email => email.credentials.stream())
      .filter(token => !token.validated)
      .forEach(token => {
        linkTo(methodOn(classOf[ConfirmationFormResource]).reSend(token.email.value))
          .withRel("resend:" + token.email.value)
          .build(link.addLink)
      })
  })

    new Confirmation
  }

  @POST
  @Produces(Array("application/json"))
  @Consumes(Array("application/json"))
  @JsonSchema(classOf[ConfirmationReturnSchema])
  @LinkDescription(value = "Confirmation", linkType = LinkType.FORM)
  def confirm(@JsonSchema(classOf[ConfirmationFormSchema]) confirmation: Confirmation): User = {
    val current = User.current()
    
    val value = current
      .emails
      .stream()
      .flatMap(email => email.credentials.stream())
      .filter{
        case web : CredentialWebAuthn => web.oneTimeToken == confirmation.code
        case _ => false  
      }
      .findFirst()
    
    if (value.isPresent)
      val userRole = Role.query(("name", "User"))
      val user = value.get()
      user.roles.clear()
      user.roles.add(userRole)
    else 
      throw new WebApplicationException(Response.Status.FORBIDDEN)
    
    current
  }

  @PUT
  @Produces(Array("application/json"))
  @JsonSchema(classOf[ConfirmationReturnSchema])
  @LinkDescription(value = "New Code", linkType = LinkType.ACTION)
  def reSend(@QueryParam("email") value : String): User = {
    val user = User.current()

    val tokens = user
      .emails
      .stream()
      .filter(email => email.value == value)
      .flatMap(email => email.credentials.stream())
      .filter(token => ! token.validated && token.isInstanceOf[CredentialWebAuthn])
      .map(token => token.asInstanceOf[CredentialWebAuthn])
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
