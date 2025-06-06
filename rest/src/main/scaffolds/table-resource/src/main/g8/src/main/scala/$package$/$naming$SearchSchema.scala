package $package$

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}

import java.lang.reflect.Type

class $naming$SearchSchema extends EntityJSONSchema[$naming$Search] {
  override def build(root: $naming$Search, javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder()

    builder.forType(classOf[$naming$Search], (builder: EntitySchemaBuilder[$naming$Search]) => builder
      .property("sort")
      .property("index")
      .property("limit")
    )

  }

}