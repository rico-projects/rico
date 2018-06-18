package dev.rico.core.spi;

import org.apiguardian.api.API;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.MAINTAINED;

@Documented
@Inherited
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface DependsOn {

    Class[] value() default {};
}
