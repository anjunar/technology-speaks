package com.anjunar.technologyspeaks.jpa;

import jakarta.inject.Qualifier;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(PostgresIndices.class)
public @interface PostgresIndex {

    String name();
    String columnList();
    String using();
    String where() default "";

}
