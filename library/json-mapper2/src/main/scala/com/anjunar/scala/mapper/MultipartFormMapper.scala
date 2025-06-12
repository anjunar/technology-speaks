package com.anjunar.scala.mapper

import com.anjunar.scala.introspector.DescriptionIntrospector
import com.anjunar.scala.mapper.file.{File, FileContext}
import com.anjunar.scala.mapper.helper.JPAHelper
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}
import com.anjunar.scala.universe.introspector.{BeanIntrospector, ScalaIntrospector}
import jakarta.persistence.OneToMany
import jakarta.ws.rs.FormParam

import java.util

class MultipartFormMapper {

  val registry = new MultipartFormConverterRegistry

  def toJava(entity : AnyRef, fields: Map[String, List[String]], files: Map[String, UploadedFile], aType: ResolvedClass, context: MultipartFormContext): AnyRef = {
    val converter = registry.find(aType)

    val model = DescriptionIntrospector.create(aType)

    model.properties.foreach(property => {

      val mapping = context.schema.findTypeMapping(aType.underlying)

      val propertyBuilder = mapping.get(property.name)

      if (propertyBuilder.isDefined) {

        if (propertyBuilder.get.writeable) {
          val converter = registry.find(property.propertyType)

          if (converter == null) {

            var value = property.get(entity).asInstanceOf[AnyRef]

            if (value == null) {
              value = context.loader.load(Map.empty, property.propertyType, Array())
            }

            toJava(value, fields, files, property.propertyType, MultipartFormContext(context, property.name, context.noValidation, propertyBuilder.get.schemaBuilder, context))

          } else {

            val formParam = property.findAnnotation(classOf[FormParam])

            if (formParam != null) {
              val option = fields.get(formParam.value())

              if (option.isDefined) {
                val value = converter.toJava(option.get, property.propertyType, context)

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
            }
          }
        }
      }

    })
    
    entity match {
      case fileContext: FileContext => 
        files.foreach((filename, uploaded) => {
          val newFile = fileContext.files.stream().filter(file => file.name == filename)
            .findFirst()
            .orElseGet(() => {
              val property = model.findProperty("files")
              val oneToMany = property.findAnnotation(classOf[OneToMany])
              val newFile = oneToMany.targetEntity().getConstructor().newInstance().asInstanceOf[File]
              fileContext.files.add(newFile)
              newFile
            })
          newFile.name = filename
          newFile.contentType = uploaded.contentType
          newFile.data = uploaded.data
        })
      case _ => // No Op
    }

    entity
  }

}
