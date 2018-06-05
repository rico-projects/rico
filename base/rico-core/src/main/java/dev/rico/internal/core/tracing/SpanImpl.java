package dev.rico.internal.core.tracing;

import dev.rico.core.context.Context;
import dev.rico.core.trace.Span;
import dev.rico.internal.core.Assert;

public class SpanImpl implements Span{

    private final brave.Span innerSpan;

    public SpanImpl(brave.Span innerSpan) {
        this.innerSpan = Assert.requireNonNull(innerSpan, "innerSpan");
    }

    @Override
    public void complete() {
        innerSpan.finish();
    }

    public void addContext(final Context context) {
        Assert.requireNonNull(context, "context");
        innerSpan.tag(context.getType(), context.getValue());
    }
}
