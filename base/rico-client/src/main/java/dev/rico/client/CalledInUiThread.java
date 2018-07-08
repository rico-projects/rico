package dev.rico.client;

import org.apiguardian.api.API;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Helper annotation for developers. All methods that are marked with this annotation are always called in the UI thread.
 * This behavior is not automatically happened by any interceptors or reflection but is handled internally in the
 * framework implementation.
 */
@Documented
@Inherited
@Retention(SOURCE)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@API(since = "0.x", status = MAINTAINED)
public @interface CalledInUiThread {
}
