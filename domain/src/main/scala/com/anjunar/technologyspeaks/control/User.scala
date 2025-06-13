package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.jpa
import com.anjunar.technologyspeaks.jpa.{PostgresIndex, PostgresIndices, RepositoryContext, Save}
import com.anjunar.technologyspeaks.openstreetmap.geocoding.GeoService
import com.anjunar.technologyspeaks.openstreetmap.geocoding2.MapBoxService
import com.anjunar.technologyspeaks.security.{IdentityContext, SecurityRole, SecurityUser}
import com.anjunar.technologyspeaks.shared.validators.Unique
import com.anjunar.technologyspeaks.shared.property.EntityView
import jakarta.enterprise.event.Observes
import jakarta.enterprise.inject.spi.CDI
import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.hibernate.`type`.SqlTypes
import org.hibernate.annotations.JdbcTypeCode

import java.util
import java.util.{HashSet, Objects, Set}
import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import org.hibernate.annotations


@Entity
@PostgresIndices(Array(
  new PostgresIndex(name = "user_idx_nickName", columnList = "nickName", using = "GIN")
))
class User extends Identity with OwnerProvider with SecurityUser {

  @Size(min = 3, max = 80)
  @NotBlank
  @PropertyDescriptor(title = "Nickname", naming = true)
  @Basic
  var nickName : String = uninitialized

  @PropertyDescriptor(title = "Password")
  @Basic
  var password : String = uninitialized

  @OneToMany(cascade = Array(CascadeType.ALL), mappedBy = "user")
  @PropertyDescriptor(title = "Emails", widget = "form-array", writeable = true)
  val emails : util.Set[EMail] = new util.HashSet[EMail]()
  
  @OneToOne(cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @PropertyDescriptor(title = "Info", naming = true)
  var info: UserInfo = uninitialized

  @OneToOne(cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @PropertyDescriptor(title = "Address")
  var address : Address = uninitialized

  @ManyToMany
  @Size(min = 1, max = 10)
  @PropertyDescriptor(title = "Roles")
  val roles: util.Set[Role] = new util.HashSet[Role]

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
  
  override def toString = s"User($nickName)"
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

  @Entity(name = "UserView")
  class View extends EntityView {
    
    override def toString = s"View()"
  }

  object View extends RepositoryContext[View](classOf[View]) {
    def findByUser(user : User) : View = {
      if (user.isPersistent) {
        try {
          User.View.query("select v from UserView v where v.user = :user")
            .setParameter("user", user)
            .getSingleResult
        } catch {
          case e: NoResultException => {
            val view = new View()
            view.user = user
            view.saveOrUpdate()
            view
          }
        }
      } else {
        null
      }
    }
  }

}