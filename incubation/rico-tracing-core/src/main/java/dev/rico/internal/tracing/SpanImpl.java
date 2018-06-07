package dev.rico.internal.tracing;

import dev.rico.core.context.Context;
import dev.rico.internal.core.Assert;
import dev.rico.tracing.Span;

import java.util.Objects;

public class SpanImpl implements Span {

    private final brave.Span innerSpan;

    public SpanImpl(final brave.Span innerSpan) {
        this.innerSpan = Assert.requireNonNull(innerSpan, "innerSpan");
    }

    @Override
    public void complete() {
        innerSpan.finish();
    }

    @Override
    public void completeExceptional(final Throwable exception) {
        Assert.requireNonNull(exception, "exception");
        innerSpan.error(exception);
    }

    @Override
    public void addContext(final Context context) {
        Assert.requireNonNull(context, "context");
        innerSpan.tag(context.getType(), context.getValue());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SpanImpl span = (SpanImpl) o;
        return Objects.equals(innerSpan, span.innerSpan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innerSpan);
    }
}
