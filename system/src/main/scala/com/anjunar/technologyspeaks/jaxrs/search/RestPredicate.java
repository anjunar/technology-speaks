package com.anjunar.technologyspeaks.jaxrs.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestPredicate {

    Class<?> value();

    String property() default "";

}
