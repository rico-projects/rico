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
package dev.rico.internal.server.projection.i18n;

import dev.rico.remoting.Property;

import java.util.Locale;
import java.util.WeakHashMap;

public class BeanLocalization {

    private final WeakHashMap<Property<String>, LocaleKey> translateableProperties = new WeakHashMap<>();

    private Locale locale;

    private Translator translator;

    public BeanLocalization(final Translator translator) {
        locale = Locale.getDefault();
        this.translator = translator;
    }

    public void add(final Property<String> property, final LocaleKey key) {
        //TODO: Exception if not in Context
        translateableProperties.put(property, key);
        update(property);
    }

    public void add(final Property<String> property, final String key, final Object... args) {
        add(property, new LocaleKey(key, args));
    }

    public void remove(final Property<String> property) {
        translateableProperties.remove(property);
    }

    private void update(final Property<String> property) {
        LocaleKey key = translateableProperties.get(property);
        if(key != null) {
            property.set(translator.translate(getLocale(), key.getKey(), key.getArgs()));
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        //TODO: Exception if not in Context
        this.locale = locale;
        translateableProperties.keySet().forEach(p -> update(p));
    }
}
