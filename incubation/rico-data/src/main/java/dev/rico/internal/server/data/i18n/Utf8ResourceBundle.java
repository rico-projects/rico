/*
 * Copyright 2018 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.internal.server.data.i18n;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


/**
 * UTF-8 friendly ResourceBundle support
 */
public class Utf8ResourceBundle {

    private final static String ISO_8859_1 = "ISO-8859-1";

    private final static String UTF_8 = "UTF-8";

    /**
     * Gets the unicode friendly resource bundle
     *
     * @param baseName
     * @return Unicode friendly resource bundle
     * @see java.util.ResourceBundle#getBundle(String)
     */
    public static final ResourceBundle getBundle(final String baseName, final Locale locale) {
        return createUtf8PropertyResourceBundle(ResourceBundle.getBundle(baseName, locale));
    }

    /**
     * Creates unicode friendly {@link java.util.PropertyResourceBundle} if possible.
     *
     * @param bundle
     * @return Unicode friendly property resource bundle
     */
    private static ResourceBundle createUtf8PropertyResourceBundle(final ResourceBundle bundle) {
        if (!(bundle instanceof PropertyResourceBundle)) {
            return bundle;
        }
        return new Utf8PropertyResourceBundle((PropertyResourceBundle) bundle);
    }

    /**
     * Resource Bundle that does the hard work
     */
    private static class Utf8PropertyResourceBundle extends ResourceBundle {

        /**
         * Bundle with unicode data
         */
        private final PropertyResourceBundle bundle;

        /**
         * Initializing constructor
         *
         * @param bundle
         */
        private Utf8PropertyResourceBundle(final PropertyResourceBundle bundle) {
            this.bundle = bundle;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Enumeration getKeys() {
            return bundle.getKeys();
        }

        @Override
        protected Object handleGetObject(final String key) {
            final String value = bundle.getString(key);
            if (value == null)
                return null;
            try {
                return new String(value.getBytes(ISO_8859_1), UTF_8);
            } catch (final UnsupportedEncodingException e) {
                throw new RuntimeException("Encoding not supported", e);
            }
        }
    }
}
