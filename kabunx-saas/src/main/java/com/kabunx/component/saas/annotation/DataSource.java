package com.kabunx.component.saas.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DataSource {
    @AliasFor("key")
    String value() default "";

    String key() default "";
}
