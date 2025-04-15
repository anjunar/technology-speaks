package com.anjunar.technologyspeaks.jaxrs.link;

import com.anjunar.scala.schema.model.LinkType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LinkDescription {

    String value();
    
    LinkType linkType();

}
