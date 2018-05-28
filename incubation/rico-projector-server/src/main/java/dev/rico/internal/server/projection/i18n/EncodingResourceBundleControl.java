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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class EncodingResourceBundleControl extends ResourceBundle.Control
{
    private final String encoding;

    public EncodingResourceBundleControl(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale,
                                    String format, ClassLoader loader,
                                    boolean reload)
            throws IllegalAccessException, InstantiationException, IOException
    {
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        URL resourceURL = loader.getResource(resourceName);
        if (resourceURL != null)
        {
            try
            {
                return new PropertyResourceBundle(new InputStreamReader(resourceURL.openStream(), encoding));
            }
            catch (Exception z)
            {
                //TODO
            }
        }

        return super.newBundle(baseName, locale, format, loader, reload);
    }
}