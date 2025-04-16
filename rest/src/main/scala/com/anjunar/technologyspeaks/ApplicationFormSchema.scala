package com.anjunar.technologyspeaks

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.*
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.security.*
import com.anjunar.technologyspeaks.shared.{ApplicationSchema, UserSchema}
import jakarta.ws.rs.core.SecurityContext

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class ApplicationFormSchema extends EntityJSONSchema[Application] {
  override def build(root: Application, javaType: Type): SchemaBuilder =
    val builder = new SchemaBuilder()
    
    ApplicationSchema.read(builder)
    UserSchema.staticForService(builder)

    builder

}

