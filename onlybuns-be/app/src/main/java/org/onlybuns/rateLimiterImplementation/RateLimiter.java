package org.onlybuns.rateLimiterImplementation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    String key() default "";

    long maxRequests() default 3;
    long timeWindow() default 1;
    TimeUnit unit() default TimeUnit.MINUTES;
}
