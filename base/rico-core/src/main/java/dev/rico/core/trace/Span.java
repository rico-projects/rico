package dev.rico.core.trace;

import dev.rico.core.context.Context;

public interface Span {

    void complete();

    void completeExceptional(Throwable e);

    void addContext(Context context);

}
