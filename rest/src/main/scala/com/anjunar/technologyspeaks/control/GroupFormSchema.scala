
package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.*
import com.anjunar.technologyspeaks.shared.{GroupSchema, RoleSchema}

import java.lang.annotation.Annotation
import java.lang.reflect.Type

class GroupFormSchema extends EntityJSONSchema[Group] {
  override def build(root: Group, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[Group], GroupSchema.dynamic(_, root))
  }
}

