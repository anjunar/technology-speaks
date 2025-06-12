package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.ApplicationFormResource
import com.anjunar.technologyspeaks.control.{Credential, CredentialWebAuthn, EMail, User}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.shared.UserSchema

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class LogoutFormSchema extends EntityJSONSchema[EMail] {
  def build(root: EMail, javaType: Type): SchemaBuilder = {
    new SchemaBuilder()
      .forType(classOf[EMail], (entity : EntitySchemaBuilder[EMail]) => entity
        .property("value")
      )
  }
}

