package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.EntitySchemaBuilder
import com.anjunar.technologyspeaks.control.{Credential, User}
import com.anjunar.technologyspeaks.document.Document
import com.anjunar.technologyspeaks.shared.editor.{Editor, Root}

object DocumentSchema {

  def static(builder: EntitySchemaBuilder[Document]): EntitySchemaBuilder[Document] = {
    builder
      .property("id")
      .property("title")
      .property("user", property => property
        .forType(classOf[User], UserSchema.staticCompact)
      )
  }

  def dynamic(builder: EntitySchemaBuilder[Document], loaded: Document): EntitySchemaBuilder[Document] = {

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
        .forType(classOf[User], builder => UserSchema.dynamicCompact(builder, loaded.user))
      )
      .property("editor", property => property
        .withWriteable(isOwnedOrAdmin)
        .forType(classOf[Editor], EditorSchema.static)
      )
  }

}
