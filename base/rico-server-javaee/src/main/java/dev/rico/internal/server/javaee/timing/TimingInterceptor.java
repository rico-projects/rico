package dev.rico.internal.server.javaee.timing;

import dev.rico.server.timing.ServerTimer;
import dev.rico.server.timing.ServerTiming;
import dev.rico.server.javaee.timing.Timing;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Optional;

@Timing
@Interceptor
public class TimingInterceptor {

    private static final String UNKNOWN_NAME = "UNKNOWN";

    @Inject
    private ServerTiming serverTiming;

    @AroundInvoke
    public Object handle(final InvocationContext joinPoint) throws Exception {
        final Method m = joinPoint.getMethod();
        final Timing timingAnnotation = m.getAnnotation(Timing.class);
        if(timingAnnotation != null) {
            final String name = Optional.ofNullable(timingAnnotation.value()).orElse(UNKNOWN_NAME);
            final String description = timingAnnotation.description();
            final ServerTimer serverTimer = serverTiming.start(name, description);
            try (serverTimer) {
                return joinPoint.proceed();
            }
        } else {
            return joinPoint.proceed();
        }
    }
}
