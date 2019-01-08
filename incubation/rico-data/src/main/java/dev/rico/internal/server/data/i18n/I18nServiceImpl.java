/*
 * Copyright 2018-2019 Karakun AG.
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

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nServiceImpl implements I18nService {

    private final String bundleBaseName;

    public I18nServiceImpl(final String bundleBaseName) {
        this.bundleBaseName = bundleBaseName;
    }

    @Override
    public String getText(final String textKey, final Locale locale) {
        return getText(bundleBaseName, textKey, locale);
    }

    @Override
    public String getText(final String resourceBaseName, final String textKey, final Locale locale) {
        ResourceBundle textBundle = Utf8ResourceBundle.getBundle(resourceBaseName, locale);
        return textBundle.getString(textKey);
    }
}
