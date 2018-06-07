package dev.rico.internal.tracing;

import brave.Span;
import dev.rico.internal.core.Assert;
import dev.rico.tracing.SpanType;

public class SpanUtils {

    public static Span.Kind convert(final SpanType spanType) {
        Assert.requireNonNull(spanType, "spanType");
        return Span.Kind.CLIENT;
    }
}
