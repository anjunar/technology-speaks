package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.{DoNotLoad, JsonSchema, NoValidation, SecuredOwner}
import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilderContext}
import com.anjunar.scala.schema.model.LinkType
import com.anjunar.technologyspeaks.control.{GeoPoint, Role, User}
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jpa.Pair
import com.anjunar.technologyspeaks.media.Media
import com.anjunar.technologyspeaks.openstreetmap.geocoding.GeoService
import com.anjunar.technologyspeaks.security.{Authenticator, EmailCredential, Secured}
import com.anjunar.technologyspeaks.shared.UserSchema
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.{EntityManager, RollbackException}
import jakarta.security.enterprise.credential.Password
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.{MediaType, Response}
import jakarta.ws.rs.core.Response.{Status, status}
import org.apache.commons.io.IOUtils
import org.hibernate.exception.ConstraintViolationException

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.{Objects, UUID}
import javax.imageio.ImageIO
import scala.compiletime.uninitialized


@Path("control/users/user")
@ApplicationScoped
@Secured
class UserFormResource extends SchemaBuilderContext {

  @Inject
  var authenticator: Authenticator = uninitialized

  @GET
  @Produces(Array("application/json"))
  @JsonSchema(classOf[UserFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Create", linkType = LinkType.FORM)
  def create: User = {
    val user = new User

    forLinks(classOf[User], (user, link) => {
      linkTo(methodOn(classOf[UserFormResource]).save(user))
        .withRel("submit")
        .build(link.addLink)
      linkTo(methodOn(classOf[UserFormResource]).delete(user.id))
        .build(link.addLink)
    })

    user
  }

  @GET
  @Path("{id}")
  @Produces(Array("application/json"))
  @JsonSchema(classOf[UserFormSchema])
  @RolesAllowed(Array("Guest", "User", "Administrator"))
  @LinkDescription(value = "Profile", linkType = LinkType.FORM)
  def read(@PathParam("id") id: UUID): User = {

    val entity = User.find(id)

    forLinks(classOf[User], (user, link) => {
      linkTo(methodOn(classOf[UserFormResource]).save(user))
        .withRel("submit")
        .build(link.addLink)
      linkTo(methodOn(classOf[UserFormResource]).delete(user.id))
        .build(link.addLink)
    })

    entity
  }

  @POST
  @Consumes(Array("application/json", MediaType.MULTIPART_FORM_DATA))
  @JsonSchema(classOf[UserFormSchema])
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Save", linkType = LinkType.FORM)
  def save(@JsonSchema(classOf[UserFormSchema]) entity: User): Response = {
    entity.saveOrUpdate()

    if (entity.info.image.data != null) {
      val in = new ByteArrayInputStream(entity.info.image.data)
      val original: BufferedImage = ImageIO.read(in)
      in.close()
      val width = original.getWidth
      val height = original.getHeight
      if (width != height) {
        val size = Math.min(width, height)
        val x = (width - size) / 2
        val y = (height - size) / 2
        val croppedView = original.getSubimage(x, y, size, size)
        val cropped = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        val g = cropped.createGraphics()
        g.drawImage(croppedView, 0, 0, null)
        g.dispose()
        val out = new ByteArrayOutputStream()
        ImageIO.write(cropped, "jpg", out)
        entity.info.image.data = out.toByteArray
      }
    }

    createRedirectResponse
  }

  @Path("/{id}")
  @DELETE
  @RolesAllowed(Array("Administrator"))
  @LinkDescription(value = "Delete", linkType = LinkType.FORM)
  def delete(@PathParam("id") id : UUID): Response = {

    val entity = User.find(id)

    val view = User.View.findByUser(entity)

    if (entity.owner == User.current() || Credential.current().hasRole("Administrator")) {
      view.delete()
      entity.delete()

      entity.entityManager.flush()
    }

    Response.ok().build()
  }
}
