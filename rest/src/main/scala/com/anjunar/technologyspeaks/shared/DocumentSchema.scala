package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.EntitySchemaBuilder
import com.anjunar.technologyspeaks.control.{Credential, User}
import com.anjunar.technologyspeaks.document.Document
import com.anjunar.technologyspeaks.shared.editor.RootNode

object DocumentSchema {

  def static(builder: EntitySchemaBuilder[Document], loaded: Document): EntitySchemaBuilder[Document] = {

    val credential = Credential.current()
    val currentUser = User.current()
    val isOwnedOrAdmin = currentUser == loaded.owner || credential.hasRole("Administrator")

    builder
      .property("id")
      .property("title", property => property
        .withTitle("Title")
        .withWriteable(true)
      )
      .property("user", property => property
        .forType(classOf[User], UserSchema.staticForService(_, isOwnedOrAdmin))
      )
      .property("root", property => property
        .withWriteable(isOwnedOrAdmin)
        .forType(classOf[RootNode], EditorSchema.static)
      )
  }

  def staticTable(builder: EntitySchemaBuilder[Document], loaded: Document): EntitySchemaBuilder[Document] = {

    val credential = Credential.current()
    val currentUser = User.current()
    val isOwnedOrAdmin = currentUser == loaded.owner || credential.hasRole("Administrator")

    builder
      .property("id")
      .property("title")
      .property("score")
      .property("user", property => property
        .forType(classOf[User], UserSchema.staticForService(_, isOwnedOrAdmin))
      )
  }


}
