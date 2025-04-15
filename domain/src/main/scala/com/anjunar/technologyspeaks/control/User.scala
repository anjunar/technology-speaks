package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.jpa
import com.anjunar.technologyspeaks.jpa.{RepositoryContext, Save}
import com.anjunar.technologyspeaks.openstreetmap.geocoding.GeoService
import com.anjunar.technologyspeaks.openstreetmap.geocoding2.MapBoxService
import com.anjunar.technologyspeaks.security.{IdentityContext, SecurityUser}
import com.anjunar.technologyspeaks.shared.validators.Unique
import com.anjunar.scala.mapper.annotations.Descriptor
import jakarta.enterprise.event.Observes
import jakarta.enterprise.inject.spi.CDI
import jakarta.persistence.*
import jakarta.validation.constraints.*

import java.util
import java.util.{HashSet, Objects, Set}
import scala.beans.BeanProperty
import scala.compiletime.uninitialized


@Entity
@Unique(message = "Benutzer schon vorhanden", property = "email")
class User extends Identity with OwnerProvider with SecurityUser {
  
  @OneToMany(cascade = Array(CascadeType.ALL), mappedBy = "user")
  @BeanProperty
  @Descriptor(title = "Emails", widget = "form-array")
  val emails : util.Set[EMail] = new util.HashSet[EMail]()
  
  @OneToOne(cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  @Descriptor(title = "Info")
  var info: UserInfo = uninitialized

  @OneToOne(cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  @Descriptor(title = "Adresse")
  var address : Address = uninitialized

  override def owner: User = this

  @PrePersist
  @PreUpdate  
  def saveGeoPoint(): Unit = {
    if (Objects.nonNull(address)) {
      val response = MapBoxService.find(address.street, address.number, address.zipCode, address.country)
      val point = new GeoPoint
      point.x = response.features.get(0).geometry.coordinates.get(0)
      point.y = response.features.get(0).geometry.coordinates.get(1)
      address.point = point
    }
  }

}

object User extends RepositoryContext[User](classOf[User]) {

  def current(): User = {
    val token = Credential.current()
    token.email.user
  }
  
  def findByEmail(email : String) : User = {
    try
      User.query("select u from User u join u.emails e where e.value = :value")
        .setParameter("value", email)
        .getSingleResult
    catch
      case e : NoResultException => null
  }

}