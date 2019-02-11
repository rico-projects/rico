package dev.rico.server.remoting;

import org.apiguardian.api.API;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@Documented
@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@API(since = "0.x", status = EXPERIMENTAL)
public @interface RemotingValue {

    String value() default "";

    boolean optional() default true;
}
