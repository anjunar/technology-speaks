package com.anjunar.technologyspeaks.configuration

import jakarta.annotation.Resource
import jakarta.ejb.Stateful
import jakarta.enterprise.inject.Produces
import jakarta.mail.Session
import jakarta.persistence.{EntityManager, PersistenceContext, ValidationMode}

import javax.sql.DataSource
import scala.compiletime.uninitialized

@Stateful 
class ResourceProvider {

  @Resource(lookup = "java:jboss/datasources/technology-speaks")
  var dataSource : DataSource = uninitialized
  
  @PersistenceContext(unitName = "main")
  var entityManager : EntityManager = uninitialized

  @Resource(name = "java:/mail/Mail")
  var session : Session = uninitialized

  @Produces
  def getEntityManager: EntityManager = {
    entityManager.setProperty("javax.persistence.validation.mode", ValidationMode.NONE)
    entityManager
  }

  @Produces 
  def getDataSource: DataSource = dataSource

  @Produces 
  def getSession: Session = session

}
