package dev.rico.internal.core.tracing;

import dev.rico.core.context.Context;
import dev.rico.core.trace.Span;
import dev.rico.internal.core.Assert;

import java.util.Objects;

public class SpanImpl implements Span{

    private final brave.Span innerSpan;

    public SpanImpl(brave.Span innerSpan) {
        this.innerSpan = Assert.requireNonNull(innerSpan, "innerSpan");
    }

    @Override
    public void complete() {
        innerSpan.finish();
    }

    @Override
    public void completeExceptional(Throwable e) {
        innerSpan.error(e);
    }

    public void addContext(final Context context) {
        Assert.requireNonNull(context, "context");
        innerSpan.tag(context.getType(), context.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpanImpl span = (SpanImpl) o;
        return Objects.equals(innerSpan, span.innerSpan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innerSpan);
    }
}
