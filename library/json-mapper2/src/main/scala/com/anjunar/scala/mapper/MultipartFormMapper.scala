package com.anjunar.scala.mapper

import com.anjunar.scala.mapper.file.FileContext
import com.anjunar.scala.mapper.helper.JPAHelper
import com.anjunar.scala.universe.ResolvedClass
import com.anjunar.scala.universe.introspector.BeanIntrospector

import java.util

class MultipartFormMapper {

  val registry = new MultipartFormConverterRegistry

  def toJava(fields: Map[String, List[String]], files: Map[String, UploadedFile], aType: ResolvedClass, context: MultipartFormContext): AnyRef = {
    val converter = registry.find(aType)

    val entity = context.loader.load(fields, aType, Array())

    val model = BeanIntrospector.create(aType)

    model.properties.foreach(property => {

      val mapping = context.schema.findTypeMapping(aType.underlying)

      val propertyBuilder = mapping(property.name)

      if (propertyBuilder.writeable) {

        val converter = context.registry.find(property.propertyType)

        val value = converter.toJava(fields(property.name), property.propertyType, context)

        val constraintViolations = context.validator.validateValue(aType.raw, property.name, value)

        context.violations.addAll(constraintViolations)

        if (constraintViolations.isEmpty) {

          value match {
            case collection: util.Collection[?] =>
              val entityCollection = property.get(entity).asInstanceOf[util.Collection[AnyRef]]
              entityCollection.clear()
              entityCollection.addAll(collection)
            case _ =>
              property.set(entity, value)
          }

          JPAHelper.resolveMappings(entity, property, value)

        }

      }

    })
    
    entity match {
      case fileContext: FileContext => 
        files.foreach((filename, uploaded) => {
          fileContext.files.stream().filter(file => file.name == filename)
            .findFirst()
            .orElseGet(() => {
              val newFile = fileContext.create
              newFile.name = filename
              newFile.contentType = uploaded.contentType
              newFile.data = uploaded.data
              newFile
            })
        })
    }

    entity
  }

}
