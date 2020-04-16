package dev.rico.server.javaee.timing;

import dev.rico.server.timing.ServerTiming;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interceptor annotation that can be used to add the timing (runtime) of a method to the server timing
 * that is send back to the client is a response.
 * Such timing can be shown in the Chrome dev tools, for example. See {@link ServerTiming} got more details.
 */
@InterceptorBinding
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Timing {

    @Nonbinding
    String value() default "";

    @Nonbinding
    String description() default "";

}
