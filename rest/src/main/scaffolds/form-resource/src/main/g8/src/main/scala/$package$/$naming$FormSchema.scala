package $package$

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, PropertyBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{createProxy, linkTo, methodOn}
import com.anjunar.technologyspeaks.media.{Media, Thumbnail}
import com.anjunar.technologyspeaks.shared.$naming$Schema

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class $naming$FormSchema extends EntityJSONSchema[$naming$] {
  override def build(root: $naming$, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[$naming$], $naming$Schema.dynamic(_, root))
  }
}

