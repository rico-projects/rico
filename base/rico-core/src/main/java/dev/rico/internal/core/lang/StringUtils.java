package dev.rico.internal.core.lang;

import java.util.Optional;

public class StringUtils {

    public static Optional<String> nonEmpty(final String s) {
        if(s == null) {
            return Optional.empty();
        }
        if(s.length() == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(s);
    }

}
