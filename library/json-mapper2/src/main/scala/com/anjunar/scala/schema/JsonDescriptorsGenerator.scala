package com.anjunar.scala.schema

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.scala.schema.analyzer.*
import com.anjunar.scala.schema.builder.{PropertyBuilder, SchemaBuilder}
import com.anjunar.scala.schema.model.validators.{NotBlankValidator, NotNullValidator, SizeValidator}
import com.anjunar.scala.schema.model.{CollectionDescriptor, EnumDescriptor, NodeDescriptor, ObjectDescriptor}
import com.anjunar.scala.universe.introspector.{BeanIntrospector, BeanProperty}
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonValue}
import jakarta.validation.constraints.{NotBlank, NotNull, Size}

import java.util
import scala.jdk.CollectionConverters.*


object JsonDescriptorsGenerator {

  private val analyzers : Array[AbstractAnalyzer] = Array(
    new PrimitiveAnalyser,
    new CollectionAnalyzer,
    new ArrayAnalyzer,
    new EnumAnalyzer,
    new ObjectAnalyzer
  )

  private def findAnalyzer(aClass : ResolvedClass) : AbstractAnalyzer = {
    val option = analyzers.find(analyzer => analyzer.analyze(aClass))
    if (option.isDefined) {
      option.get
    } else {
      throw new IllegalStateException("no Analyzer found : " + aClass.raw.getName)
    }
  }
  
  def generateObject(aClass : ResolvedClass, schema: SchemaBuilder) : ObjectDescriptor = {

    val beanModel = BeanIntrospector.create(aClass)

    val descriptor = new ObjectDescriptor
    descriptor.`type` = aClass.raw.getSimpleName

    beanModel.properties.foreach(property => {

      val option = if (schema.table) {
        val descriptor = property.findAnnotation(classOf[Descriptor])
        if (descriptor == null) {
          None
        } else {
          Some(new PropertyBuilder[Any](property.name, aClass.raw))
        }
      } else {
        schema.typeMapping(aClass.raw).mapping.get(property.name)        
      }
      
      if (option.isDefined) {
        val schemaDefinition = option.get

        findAnalyzer(property.propertyType) match
          case p: PrimitiveAnalyser =>
            val nodeDescriptor: NodeDescriptor = generatePrimitive(property, schemaDefinition)
            descriptor.properties.put(property.name, nodeDescriptor)
          case e : EnumAnalyzer =>
            val enumDescriptor: EnumDescriptor = generateEnum(property, schemaDefinition)
            descriptor.properties.put(property.name, enumDescriptor)
          case c : CollectionAnalyzer =>
            val collectionDescriptor = generateArray(property, schema, schemaDefinition)
            collectionDescriptor.links.putAll(schemaDefinition.links.asJava)
            descriptor.properties.put(property.name, collectionDescriptor)
          case c : ArrayAnalyzer =>
            val collectionDescriptor = generatePrimitive(property, schemaDefinition)
            descriptor.properties.put(property.name, collectionDescriptor)
          case o : ObjectAnalyzer =>
            val objectDescriptor: ObjectDescriptor = generateObject(schema, property, schemaDefinition)
            descriptor.properties.put(property.name, objectDescriptor)
      }
    })

    val jsonSubTypes = aClass.findDeclaredAnnotation(classOf[JsonSubTypes])
    if (jsonSubTypes != null) {
      jsonSubTypes
        .value()
        .foreach(subType => descriptor.oneOf.add(JsonDescriptorsGenerator.generateObject(TypeResolver.resolve(subType.value()), schema)))
    }

    descriptor
  }

  private def generateValidator(property: BeanProperty, descriptor : NodeDescriptor) : Unit = {
    property.annotations.foreach {
      case size: Size =>
        descriptor.validators.put("Size", SizeValidator(size.min(), size.max()))
      case notBlank: NotBlank =>
        descriptor.validators.put("NotBlank", NotBlankValidator())
      case notNull: NotNull =>
        descriptor.validators.put("NotNull", NotNullValidator())
      case _ => {}
    }
  }

  private def generatePrimitive(property: BeanProperty, schemaDefinition: PropertyBuilder[?]) = {
    val nodeDescriptor = NodeDescriptor(
      schemaDefinition.title,
      schemaDefinition.description,
      schemaDefinition.widget,
      schemaDefinition.id,
      schemaDefinition.naming,
      schemaDefinition.writeable,
      property.propertyType.raw.getSimpleName,
      schemaDefinition.step,
      schemaDefinition.links.asJava
    )
    generateValidator(property, nodeDescriptor)
    nodeDescriptor
  }

  private def generateEnum(property: BeanProperty, schemaDefinition: PropertyBuilder[?]) = {
    val constants: Array[Enum[?]] = property.propertyType.raw.getEnumConstants.asInstanceOf[Array[Enum[?]]]
    val enums = constants.map(constant => {
      val enumClass = TypeResolver.resolve(constant.getClass)
      val option = enumClass.declaredMethods.find(method => method.findDeclaredAnnotation(classOf[JsonValue]) != null)
      if (option.isDefined) {
        val method = option.get
        val value = method.invoke(constant)
        value.asInstanceOf[String]
      } else {
        constant.name()
      }
    })
    val enumDescriptor = EnumDescriptor(
      schemaDefinition.title,
      schemaDefinition.description,
      schemaDefinition.widget,
      schemaDefinition.id,
      schemaDefinition.naming,
      schemaDefinition.writeable,
      property.propertyType.raw.getSimpleName,
      schemaDefinition.links.asJava,
      enums.toList.asJava
    )
    generateValidator(property, enumDescriptor)
    enumDescriptor
  }

  private def generateObject(schema: SchemaBuilder, property: BeanProperty, schemaDefinition: PropertyBuilder[?]): ObjectDescriptor = {
    val propertyType = property.propertyType
    val objectDescriptor = generateObject(propertyType, schema)
    objectDescriptor.title = schemaDefinition.title
    objectDescriptor.description = schemaDefinition.description
    objectDescriptor.widget = schemaDefinition.widget
    objectDescriptor.id = schemaDefinition.id
    objectDescriptor.name = schemaDefinition.naming
    objectDescriptor.writeable = schemaDefinition.writeable
    generateValidator(property, objectDescriptor)
    objectDescriptor
  }

  private def generateArray(property: BeanProperty, schema: SchemaBuilder, schemaDefinition: PropertyBuilder[?]): CollectionDescriptor = {
    val descriptor = new CollectionDescriptor
    val collectionType = property.propertyType.typeArguments.head

    descriptor.items = generateObject(collectionType, schema)
    descriptor.`type` = property.propertyType.raw.getSimpleName
    descriptor.title = schemaDefinition.title
    descriptor.description = schemaDefinition.description
    descriptor.widget = schemaDefinition.widget
    descriptor.id = schemaDefinition.id
    descriptor.name = schemaDefinition.naming
    descriptor.writeable = schemaDefinition.writeable
    generateValidator(property, descriptor)
    descriptor
  }
  
}
