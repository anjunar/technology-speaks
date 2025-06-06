package $package$

import com.anjunar.scala.mapper.annotations.JsonSchema
import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.RoleSearch
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.$naming$Schema

import java.lang.annotation.Annotation
import java.lang.reflect.Type


class $naming$TableSchema extends EntityJSONSchema[Table[$naming$]] {
  override def build(root: Table[$naming$], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[Table[$naming$]]) => builder
      .property("rows", property => property
        .withTitle("$naming$s")
        .forType(classOf[$naming$], builder => $naming$Schema.static(builder))
        .forInstance(root.rows, classOf[$naming$], (entity : $naming$) => (builder  : EntitySchemaBuilder[$naming$]) => $naming$Schema.dynamic(builder, entity))
      )
      .property("size")
    )
  }

}