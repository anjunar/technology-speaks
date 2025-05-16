package com.anjunar.technologyspeaks.timeline

import com.anjunar.scala.schema.builder.{EntityJSONSchema, EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.control.{RoleTableSearch, User, UserTableResource}
import com.anjunar.technologyspeaks.jaxrs.link.WebURLBuilderFactory.{linkTo, methodOn}
import com.anjunar.technologyspeaks.jaxrs.types.{QueryTable, Table}
import com.anjunar.technologyspeaks.shared.{PostSchema, UserSchema}

import java.lang.reflect.Type

class PostTableSchema extends EntityJSONSchema[QueryTable[PostTableSearch, Post]] {
  override def build(root: QueryTable[PostTableSearch, Post], javaType: Type): SchemaBuilder = {
    val builder = new SchemaBuilder(true)

    builder.forType(javaType, (builder: EntitySchemaBuilder[QueryTable[PostTableSearch, Post]]) => builder
      .property("search", property => property
        .forType(classOf[PostTableSearch], (builder: EntitySchemaBuilder[PostTableSearch]) => builder
          .property("sort")
          .property("index")
          .property("limit")
          .property("user", property => property
            .forType(classOf[User], UserSchema.static)
            .withLinks(links => {
              linkTo(methodOn(classOf[UserTableResource]).list(null))
                .build(links.addLink)
            })
          )
        )
      )
      .property("rows", property => property
        .withTitle("Posts")
        .forType(classOf[Post], builder => PostSchema.static(builder))
        .forInstance(root.rows, classOf[Post], (entity : Post) => builder => PostSchema.dynamic(builder, entity))
      )
      .property("size")
    )
  }

}