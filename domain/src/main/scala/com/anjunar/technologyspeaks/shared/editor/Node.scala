package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import jakarta.json.bind.annotation.JsonbTypeInfo
import jakarta.persistence.{Entity, Inheritance, InheritanceType}


@JsonSubTypes(Array(
  new JsonSubTypes.Type(value = classOf[Code], name = "code"),
  new JsonSubTypes.Type(value = classOf[Emphasis], name = "emphasis"),
  new JsonSubTypes.Type(value = classOf[Heading], name = "heading"),
  new JsonSubTypes.Type(value = classOf[Image], name = "image"),
  new JsonSubTypes.Type(value = classOf[List], name = "list"),
  new JsonSubTypes.Type(value = classOf[ListItem], name = "listItem"),
  new JsonSubTypes.Type(value = classOf[Paragraph], name = "paragraph"),
  new JsonSubTypes.Type(value = classOf[Root], name = "root"),
  new JsonSubTypes.Type(value = classOf[Strong], name = "strong"),
  new JsonSubTypes.Type(value = classOf[Table], name = "table"),
  new JsonSubTypes.Type(value = classOf[TableCell], name = "tableCell"),
  new JsonSubTypes.Type(value = classOf[TableRow], name = "tableRow"),
  new JsonSubTypes.Type(value = classOf[Text], name = "text")
))
@JsonTypeInfo(use = Id.NAME, property = "type")
abstract class Node
