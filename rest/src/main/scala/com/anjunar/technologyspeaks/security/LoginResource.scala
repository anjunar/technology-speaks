package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{createProxy, linkTo, methodOn}
import com.yubico.webauthn.*
import com.yubico.webauthn.data.*
import jakarta.enterprise.context.SessionScoped
import jakarta.inject.Inject
import jakarta.security.enterprise.AuthenticationStatus
import jakarta.security.enterprise.credential.UsernamePasswordCredential
import jakarta.ws.rs.*
import jakarta.ws.rs.core.*

import java.net.URI
import scala.compiletime.uninitialized

@Path("/security")
@Produces(Array(MediaType.APPLICATION_JSON))
@Consumes(Array(MediaType.APPLICATION_JSON))
@SessionScoped
class LoginResource extends Serializable with SchemaBuilderContext {

  @Inject
  var webAuthnService: WebAuthnService = uninitialized

  @Inject
  var authenticator: Authenticator = uninitialized

  var assertionRequest: AssertionRequest = uninitialized

  @GET
  @Path("login")  
  @JsonSchema(classOf[LoginSchema])
  @LinkDescription(value = "Login", linkType = LinkType.FORM)  
  def entry(): Login = {

    forLinks(classOf[Login], (instance, link) => {
      linkTo(methodOn(classOf[LoginResource]).beginLogin(null))
        .withRel("login")
        .build(link.addLink)
      linkTo(methodOn(classOf[LoginResource]).fallback(null))
        .withRel("submit")
        .build(link.addLink)
    })


    new Login
  }
  
  @POST
  @Path("fallback")
  @LinkDescription(value = "Login", linkType = LinkType.FORM)
  @Consumes(Array(MediaType.APPLICATION_FORM_URLENCODED))  
  def fallback(@BeanParam login : Login) : Response = {
    
    val credential = new UsernamePasswordCredential(login.username, login.password)

    val status = authenticator.authenticate(credential)

    status match {
      case AuthenticationStatus.SUCCESS =>
        createRedirectResponse
      case _ =>
        Response.status(Response.Status.UNAUTHORIZED).build()
    }
  }

  @POST
  @Path("options")
  @LinkDescription(value = "Login", linkType = LinkType.FORM)
  def beginLogin(requestBody: Login): Response = {
    val rp = webAuthnService.relyingParty

    val options = rp.startAssertion(
      StartAssertionOptions.builder()
        .username(requestBody.username)
        .build()
    )

    assertionRequest = options

    Response.ok(options.toCredentialsGetJson).build()
  }

  @POST
  @Path("/finish")
  def finishLogin(loginFinish: PublicKeyCredential[AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs]): Response = {
    val rp = webAuthnService.relyingParty

    val options = assertionRequest

    val result = rp.finishAssertion(FinishAssertionOptions.builder()
      .request(options)
      .response(loginFinish)
      .build())

    val credential = WebAuthnCredential(
      result.getUsername,
      result.getCredential.getCredentialId,
      result.getCredential.getUserHandle
    )

    authenticator.logout()
    
    val status = authenticator.authenticate(credential)

    status match {
      case AuthenticationStatus.SUCCESS =>
        Response.ok().build()
      case _ =>
        Response.status(Response.Status.UNAUTHORIZED).build()
    }
  }
}
