package com.anjunar.scala.mapper

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.mapper.exceptions.{ValidationException, ValidationViolation}
import com.anjunar.scala.mapper.intermediate.model.{JsonNode, JsonObject}
import com.anjunar.scala.mapper.loader.EntityLoader
import com.anjunar.scala.mapper.{Context, ConverterRegistry, JsonMapper}
import com.anjunar.scala.schema.{JsonDescriptorsContext, JsonDescriptorsGenerator}
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilderProvider}
import com.anjunar.scala.schema.model.ObjectDescriptor
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}
import jakarta.inject.Inject
import jakarta.validation.{ConstraintViolation, ValidatorFactory}
import jakarta.ws.rs.core.{MediaType, MultivaluedMap}
import jakarta.ws.rs.ext.{MessageBodyReader, MessageBodyWriter, Provider}
import jakarta.ws.rs.{Consumes, Produces, WebApplicationException}

import java.io.{IOException, InputStream, OutputStream}
import java.lang.annotation.Annotation
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.util
import java.util.UUID
import scala.collection.mutable
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*


@Provider
@Consumes(Array("application/json", "application/*+json", "text/json"))
@Produces(Array("application/json", "application/*+json", "text/json"))
class JsonProvider extends MessageBodyReader[AnyRef] with MessageBodyWriter[AnyRef] {

  val jsonMapper = new JsonMapper()

  val registry = new ConverterRegistry()

  @Inject
  var validatorFactory: ValidatorFactory = uninitialized

  @Inject
  var entityLoader : EntityLoader = uninitialized

  @Inject
  var callback : Callback = uninitialized

  @Inject
  var schemaProvider : SchemaBuilderProvider = uninitialized

  override def isReadable(aClass: Class[?], javaType: Type, annotations: Array[Annotation], mediaType: MediaType): Boolean = {
    annotations.exists(annotation => annotation.annotationType() == classOf[JsonSchema])
  }

  @throws[IOException]
  @throws[WebApplicationException]
  override def readFrom(aClass: Class[AnyRef], javaTypeRaw: Type, annotations: Array[Annotation], mediaType: MediaType, multivaluedMap: MultivaluedMap[String, String], inputStream: InputStream): AnyRef = {

    val resolvedClass = TypeResolver.resolve(javaTypeRaw)

    val jsonString = new String(inputStream.readAllBytes())

    val jsonSchemaAnnotation = annotations
      .find(annotation => annotation.annotationType() == classOf[JsonSchema])
      .get
      .asInstanceOf[JsonSchema]

    val jsonSchema : EntityJSONSchema[Any] = jsonSchemaAnnotation.value().getConstructor().newInstance().asInstanceOf[EntityJSONSchema[Any]]

    val jsonObject = jsonMapper.toJsonObjectForJava(jsonString)

    val entity = entityLoader.load(jsonObject, TypeResolver.resolve(javaTypeRaw), annotations)

    val schemaBuilder = jsonSchema.build(entity, javaTypeRaw)
    
    val context = new Context(null, validatorFactory.getValidator, registry, schemaBuilder, entityLoader)

    val value = jsonMapper.toJava(jsonObject, resolvedClass, context)

    val path = new util.ArrayList[AnyRef]()

    val violations : mutable.ListBuffer[ValidationViolation] = new mutable.ListBuffer[ValidationViolation]()

    traverseContext(context, path, (path, context) => {
      violations.addAll(extractViolations(context, path))
    })

    if (violations.isEmpty) {
      callback.call(value, annotations)
    } else {
      throw new ValidationException(violations.asJava)
    }
  }

  private def traverseContext(context : Context, path : util.List[AnyRef], callback : (util.List[AnyRef], Context) => Unit): Unit = {
    callback(path, context)
    context.children.foreach(entry => {
      val paths = new util.ArrayList[AnyRef](path)
      paths.add(entry._1)
      traverseContext(entry._2, paths, callback)
    })
  }

  private def extractViolations(context: Context, path: util.List[AnyRef]): mutable.Set[ValidationViolation] = {
    context
      .violations
      .asScala
      .map(violation => {
        val newPath = util.ArrayList[AnyRef](path)
        newPath.add(violation.getPropertyPath.toString)
        new ValidationViolation(newPath, violation.getMessage, violation.getRootBeanClass)
      })
  }

  override def isWriteable(aClass: Class[?], javaType: Type, annotations: Array[Annotation], mediaType: MediaType): Boolean = {
    annotations.exists(annotation => annotation.annotationType() == classOf[JsonSchema])
  }

  @throws[IOException]
  @throws[WebApplicationException]
  override def writeTo(element: AnyRef, aClass: Class[?], javaTypeRaw: Type, annotations: Array[Annotation], mediaType: MediaType, multivaluedMap: MultivaluedMap[String, AnyRef], outputStream: OutputStream): Unit = {

    val resolvedClass = TypeResolver.resolve(javaTypeRaw)

    val jsonSchemaAnnotation = annotations
      .find(annotation => annotation.annotationType() == classOf[JsonSchema])
      .get
      .asInstanceOf[JsonSchema]
    
    val validator = validatorFactory.getValidator

    val jsonSchema = jsonSchemaAnnotation.value().getConstructor().newInstance().asInstanceOf[EntityJSONSchema[Any]]

    val schema = jsonSchema.build(element, javaTypeRaw)

    schemaProvider.builder.typeMapping.foreachEntry((clazz, builder) => {
      schema.findEntitySchemaDeepByClass(clazz).foreach(b => b.links = builder.links)
    })
    schemaProvider.builder.instanceMapping.foreachEntry((instance, builder) => {
      schema.findEntitySchemaDeepByInstance(instance).foreach(b => b.links = builder.links)
    })

    val context = new Context(null, validator, registry, schema, null)

    val jsonObject = jsonMapper.toJson(element, resolvedClass, context)

    val properties = jsonObject.value
    val objectDescriptor = JsonDescriptorsGenerator.generateObject(resolvedClass, schema, new JsonDescriptorsContext(null))

    val contextForDescriptor = new Context(null, validatorFactory.getValidator, registry, schema, null)
    val jsonDescriptor = jsonMapper.toJson(objectDescriptor, TypeResolver.resolve(classOf[ObjectDescriptor]), contextForDescriptor)
    
    properties.put("$descriptors", jsonDescriptor)

    val string  = jsonMapper.toJsonObjectForJson(jsonObject)
    
    outputStream.write(string .getBytes(StandardCharsets.UTF_8))
  }
}
