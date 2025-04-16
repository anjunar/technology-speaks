package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.yubico.webauthn.*
import com.yubico.webauthn.data.*
import jakarta.enterprise.context.SessionScoped
import jakarta.inject.Inject
import jakarta.security.enterprise.AuthenticationStatus
import jakarta.ws.rs.*
import jakarta.ws.rs.core.*

import scala.compiletime.uninitialized

@Path("/security")
@Produces(Array(MediaType.APPLICATION_JSON))
@Consumes(Array(MediaType.APPLICATION_JSON))
@SessionScoped
class WebAuthnLoginResource extends Serializable with SchemaBuilderContext {

  @Inject
  var webAuthnService: WebAuthnService = uninitialized

  @Inject
  var authenticator: Authenticator = uninitialized

  var assertionRequest: AssertionRequest = uninitialized
  
  @GET
  @Path("login")  
  @JsonSchema(classOf[WebAuthnLoginSchema])
  @LinkDescription(value = "Login", linkType = LinkType.FORM)  
  def entry(): WebAuthnLogin = {

    provider.builder
      .forType(classOf[WebAuthnLogin], (entity: EntitySchemaBuilder[WebAuthnLogin]) => entity
        .withLinks((instance, link) => {
          linkTo(methodOn(classOf[WebAuthnLoginResource]).beginLogin(null))
            .withRel("login")
            .build(link.addLink)
        })
      )


    new WebAuthnLogin
  }

  @POST
  @Path("/options")
  @LinkDescription(value = "Login", linkType = LinkType.FORM)
  def beginLogin(requestBody: WebAuthnLogin): Response = {
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
