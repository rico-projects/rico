package dev.rico.tracing;

import dev.rico.core.context.Context;

import java.util.Optional;

public interface Span {

    String getTraceId();

    String getSpanId();

    Optional<String> getParentSpanId();

    void complete();

    void completeExceptional(Throwable e);

    void addContext(Context context);

}
