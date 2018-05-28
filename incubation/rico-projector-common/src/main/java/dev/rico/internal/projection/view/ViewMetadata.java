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
package dev.rico.internal.projection.view;

import dev.rico.internal.projection.base.View;
import dev.rico.internal.projection.base.WithLayoutMetadata;
import dev.rico.internal.projection.metadata.MetadataUtilities;
import dev.rico.remoting.BeanManager;

import java.util.Optional;

public interface ViewMetadata {

    String JAVAFX_LAYOUT_ORIENTATION = "orientation";

    String JAVAFX_LAYOUT_ORIENTATION_VALUE_HORIZONTAL = "horizontal";

    String JAVAFX_LAYOUT_ORIENTATION_VALUE_VERTICAL = "vertical";

    String JAVAFX_LAYOUT_CONTENT_GROW = "grow";

    String JAVAFX_LAYOUT_MARGIN = "margin";

    String JAVAFX_LAYOUT_BACKGROUND_COLOR = "backgroundColor";

    String JAVAFX_LAYOUT_CONTENT_GROW_VALUE_ALWAYS = "ALWAYS";

    String JAVAFX_LAYOUT_CONTENT_GROW_VALUE_NEVER = "NEVER";

    String JAVAFX_LAYOUT_CONTENT_GROW_VALUE_SOMETIMES = "SOMETIMES";

    static void setMargin(WithLayoutMetadata content, BeanManager beanManager, double margin) {
        MetadataUtilities.getOrCreateDoubleBasedMetadata(JAVAFX_LAYOUT_MARGIN, content, beanManager).setValue(margin);
    }

    static void setBackgroundColor(WithLayoutMetadata content, BeanManager beanManager, String colorInHex) {
        MetadataUtilities.getOrCreateStringBasedMetadata(JAVAFX_LAYOUT_BACKGROUND_COLOR, content, beanManager).setValue(colorInHex);
    }

    static String getBackgroundColor(WithLayoutMetadata content) {
        return  MetadataUtilities.getMetadata(JAVAFX_LAYOUT_BACKGROUND_COLOR, content).map(key -> Optional.ofNullable(key.getValue()).orElse("").toString()).orElse("");
    }

    static void contentShouldNeverGrow(WithLayoutMetadata content, BeanManager beanManager) {
        MetadataUtilities.getOrCreateStringBasedMetadata(JAVAFX_LAYOUT_CONTENT_GROW, content, beanManager).setValue(JAVAFX_LAYOUT_CONTENT_GROW_VALUE_NEVER);
    }

    static void contentShouldAlwaysGrow(WithLayoutMetadata content, BeanManager beanManager) {
        MetadataUtilities.getOrCreateStringBasedMetadata(JAVAFX_LAYOUT_CONTENT_GROW, content, beanManager).setValue(JAVAFX_LAYOUT_CONTENT_GROW_VALUE_ALWAYS);
    }

    static void setOrientationToHorizontal(View view, BeanManager beanManager) {
        MetadataUtilities.getOrCreateStringBasedMetadata(JAVAFX_LAYOUT_ORIENTATION, view, beanManager).setValue(JAVAFX_LAYOUT_ORIENTATION_VALUE_HORIZONTAL);
    }

    static void setOrientationToVertical(View view, BeanManager beanManager) {
        MetadataUtilities.getOrCreateStringBasedMetadata(JAVAFX_LAYOUT_ORIENTATION, view, beanManager).setValue(JAVAFX_LAYOUT_ORIENTATION_VALUE_VERTICAL);
    }

    static boolean isOrientationVertical(View view) {
        return  MetadataUtilities.getMetadata(JAVAFX_LAYOUT_ORIENTATION, view).map(m -> {
            String value = Optional.ofNullable(m.getValue()).orElse("").toString();
            return value.equals(JAVAFX_LAYOUT_ORIENTATION_VALUE_VERTICAL);
        }).orElse(false);
    }

}
