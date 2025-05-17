package com.anjunar.scala.schema.model

import com.anjunar.scala.mapper.annotations.IgnoreFilter
import com.anjunar.scala.schema.builder.SchemaBuilder

import scala.beans.BeanProperty

@IgnoreFilter
case class Link(@BeanProperty url: String,
                @BeanProperty method : String,
                @BeanProperty rel : String,
                @BeanProperty title: String,
                @BeanProperty linkType : LinkType,
                @BeanProperty body : AnyRef)
