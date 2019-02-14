package dev.rico.server.remoting;

import org.apiguardian.api.API;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Annotation to define inital parameters of a {@link RemotingController}. Such parameters
 * can be defined on the client side when requesting a controller(proxy). By doing so such
 * parameters will automatically be injected in the new controller instance before any
 * {@link javax.annotation.PostConstruct} method is called.
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@API(since = "0.x", status = EXPERIMENTAL)
public @interface RemotingValue {

    /**
     * Defines the name of the value. If the default ("" - empty string) is used the name of the
     * annotated field will be used instead.
     * @return the name of the value
     */
    String value() default "";

    /**
     * Defines if the value is an optional value. If a value is not optional and no parameter for
     * the value is defined, the controller can not be created and an exception will be thrown.
     * @return true if the value is optional otherwise false
     */
    boolean optional() default true;
}
