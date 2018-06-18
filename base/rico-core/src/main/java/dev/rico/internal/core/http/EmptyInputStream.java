package dev.rico.internal.core.http;

import java.io.InputStream;

public class EmptyInputStream extends InputStream {

    @Override
    public int read() {
        return -1;
    }
}
