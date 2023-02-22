package com.kabunx.component.saas.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DS {
    @AliasFor("key")
    String value() default "";

    @AliasFor("name")
    String key() default "";
}
