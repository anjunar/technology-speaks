package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.{Credential, CredentialWebAuthn, EMail, Role, User}
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jpa.Pair
import com.yubico.webauthn.*
import com.yubico.webauthn.data.*
import com.yubico.webauthn.exception.RegistrationFailedException
import jakarta.annotation.Resource
import jakarta.enterprise.context.SessionScoped
import jakarta.inject.Inject
import jakarta.transaction.{Synchronization, Transaction, TransactionManager, TransactionSynchronizationRegistry}
import jakarta.ws.rs.*
import jakarta.ws.rs.core.{MediaType, Response}
import org.slf4j.LoggerFactory

import java.security.SecureRandom
import java.util
import java.util.Optional
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

@Path("/security")
@Produces(Array(MediaType.APPLICATION_JSON))
@Consumes(Array(MediaType.APPLICATION_JSON))
@SessionScoped
class RegistrationResource extends Serializable with SchemaBuilderContext {

  val logger = LoggerFactory.getLogger(classOf[RegistrationResource])

  var request: PublicKeyCredentialCreationOptions = uninitialized

  @Inject
  var webAuthnService: WebAuthnService = uninitialized

  @Resource
  var transaction : TransactionSynchronizationRegistry = uninitialized

  @Inject
  var mailService : MailService = uninitialized

  @GET
  @Path("register")
  @JsonSchema(classOf[RegistrationSchema])
  @LinkDescription(value = "Register", linkType = LinkType.FORM)
  def entry(): Login = {

    forLinks(classOf[Login], (instance, link) => {
      linkTo(methodOn(classOf[RegistrationResource]).generateRegistrationOptions(null))
        .withRel("register")
        .build(link.addLink)
    })


    new Login
  }

  def findExistingUser(userName: String, displayName: String): Optional[UserIdentity] = {
    val credential = CredentialWebAuthn.forUserNameAndDisplayName(userName, displayName)
    if (credential == null) {
      Optional.empty()
    } else {
      Optional.of(UserIdentity
        .builder()
        .name(credential.email.value)
        .displayName(credential.displayName)
        .id(new ByteArray(credential.email.handle))
        .build()
      )
    }
  }

  @POST
  @Path("/register-options")
  @LinkDescription(value = "Register", linkType = LinkType.FORM)
  def generateRegistrationOptions(userRequest: Login): Response = {
    request = webAuthnService
      .relyingParty
      .startRegistration(StartRegistrationOptions
        .builder
        .user(findExistingUser(userRequest.username, userRequest.displayName).orElseGet(() => {

          val email = EMail.query(("value", userRequest.username))

          val userHandle = if (email == null || email.handle == null) {
            val userHandle = new Array[Byte](64)
            val random = new SecureRandom()
            random.nextBytes(userHandle)
            userHandle
          } else {
            email.handle
          }


          UserIdentity
            .builder
            .name(userRequest.username)
            .displayName(userRequest.displayName)
            .id(new ByteArray(userHandle))
            .build
        }))
        .build
      )

    Response.ok(request.toCredentialsCreateJson).build()
  }

  @POST
  @Path("/register-finish")
  def finishRegistration(pkc: PublicKeyCredential[AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs]): util.Map[String, Boolean] = {

    try
      val result: RegistrationResult = webAuthnService.relyingParty.finishRegistration(
        FinishRegistrationOptions.builder()
          .request(request)
          .response(pkc)
          .build()
      )

      val guest = Role.query(("name", "Guest"))

      var user = User.findByEmail(request.getUser.getName)
      val token = if (user == null) {

        val token = new CredentialWebAuthn
        token.roles.add(guest)
        token.generateOneTimeToken()

        val email = new EMail
        token.email = email
        email.value = request.getUser.getName
        email.handle = request.getUser.getId.getBytes
        email.credentials.add(token)

        user = new User
        email.user = user
        user.emails.add(email)
        user.enabled = true
        user.saveOrUpdate()

        token
      } else {
        val mail = user.emails
          .stream()
          .filter(email => email.value == request.getUser.getName)
          .findFirst()
          .get()

        mail.handle = request.getUser.getId.getBytes

        val administrator = mail.credentials
          .stream()
          .filter({
            case token : CredentialWebAuthn => token.hasRole("Administrator") && token.credentialId == null
            case _ => false  
          })
          .findFirst()
          .asInstanceOf[Optional[CredentialWebAuthn]]

        val token = if (administrator.isPresent) {
          administrator.get()
        } else {
          val token = new CredentialWebAuthn
          token.roles.add(guest)
          token.generateOneTimeToken()

          mail.credentials.add(token)
          token.email = mail
          token
        }

        token
      }

      token.displayName = request.getUser.getDisplayName
      token.credentialId = result.getKeyId.getId.getBytes
      token.publicKeyCose = result.getPublicKeyCose.getBytes
      token.signCount = result.getSignatureCount
      token.transports = result.getKeyId.getTransports.get().asScala.mkString(",")

      val name = request.getUser.getName

      transaction.registerInterposedSynchronization(new Synchronization {
        override def beforeCompletion(): Unit = {}

        override def afterCompletion(status: Int): Unit = {

          try {
            val variables = util.HashMap[String, AnyRef]()
            variables.put("token", token)

            mailService.send(name, variables, "/templates/RegisterTemplate.html", "Technology Speaks - Registration")
          } catch {
            case e: Exception => logger.error(e.getMessage, e)
          }

        }
      })

      Map("success" -> true).asJava

    catch
      case e: RegistrationFailedException => {
        Map("success" -> false).asJava
      }
  }

}


