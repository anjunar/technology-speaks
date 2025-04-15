package com.anjunar.scala.schema.model

import com.anjunar.scala.mapper.annotations.IgnoreFilter

import scala.beans.BeanProperty

@IgnoreFilter
class Link(@BeanProperty val url: String, 
           @BeanProperty val method : String,
           @BeanProperty val rel : String,
           @BeanProperty val title: String,
           @BeanProperty val linkType : LinkType)
