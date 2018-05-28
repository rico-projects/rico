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
package dev.rico.internal.server.projection.projection;

import dev.rico.internal.projection.base.Icon;
import dev.rico.internal.projection.base.IconBean;
import dev.rico.internal.projection.form.FormField;
import dev.rico.internal.projection.form.FormSection;
import dev.rico.internal.projection.form.FormSectionBean;
import dev.rico.internal.projection.metadata.AbstractKeyValueBean;
import dev.rico.internal.projection.metadata.KeyValue;
import dev.rico.internal.server.projection.i18n.BeanLocalization;
import dev.rico.internal.server.projection.i18n.LocaleKey;
import dev.rico.remoting.BeanManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormSectionBuilder<T extends FormSection> {

    private final Class<T> formSectionClass;

    private final BeanManager beanManager;

    private String description;

    private String title;

    private String iconFamily;

    private String iconCode;

    private List<FormField> fields = new ArrayList<>();

    private Map<String, Object> metadata = new HashMap<>();

    private final BeanLocalization localization;

    private LocaleKey descriptionLocaleKey;

    private LocaleKey titleLocaleKey;

    protected FormSectionBuilder(Class<T> formSectionClass, BeanManager beanManager, BeanLocalization localization) {
        this.formSectionClass = formSectionClass;
        this.beanManager = beanManager;
        this.localization = localization;
    }

    protected FormSectionBuilder(Class<T> formSectionClass, BeanManager beanManager) {
        this(formSectionClass, beanManager, null);
    }

    public FormSectionBuilder<T> withIcon(String iconFamily, String iconCode) {
        return this;
    }

    public FormSectionBuilder<T> withDescription(String description) {
        this.description = description;
        this.descriptionLocaleKey = null;
        return this;
    }

    public FormSectionBuilder<T> withLocalizedDescription(String key, Object... args) {
        if(localization == null) {
            throw new NullPointerException("localization == null");
        }
        this.description = null;
        this.descriptionLocaleKey = new LocaleKey(key, args);
        return this;
    }

    public FormSectionBuilder<T> withTitle(String title) {
        this.title = title;
        this.titleLocaleKey = null;
        return this;
    }

    public FormSectionBuilder<T> withLocalizedTitle(String key, Object... args) {
        if(localization == null) {
            throw new NullPointerException("localization == null");
        }
        this.title = null;
        this.titleLocaleKey = new LocaleKey(key, args);
        return this;
    }

    public FormSectionBuilder<T> withLayoutMetadata(String key, String value) {
        metadata.put(key, value);
        return this;
    }

    public FormSectionBuilder<T> withLayoutMetadata(String key, Integer value) {
        metadata.put(key, value);
        return this;
    }

    public FormSectionBuilder<T> withLayoutMetadata(String key, Double value) {
        metadata.put(key, value);
        return this;
    }

    public FormSectionBuilder<T> withLayoutMetadata(String key, Long value) {
        metadata.put(key, value);
        return this;
    }

    public FormSectionBuilder<T> withLayoutMetadata(String key, Float value) {
        metadata.put(key, value);
        return this;
    }

    public FormSectionBuilder<T> withLayoutMetadata(String key, Boolean value) {
        metadata.put(key, value);
        return this;
    }

    public FormSectionBuilder<T> removeLayoutMetadata(String key) {
        metadata.remove(key);
        return this;
    }

    public <U> FormSectionBuilder<T> withField(FormField<U> field) {
        fields.add(field);
        return this;
    }

    public T build() {
        T formSection = beanManager.create(formSectionClass);

        if(titleLocaleKey == null) {
            formSection.setTitle(title);
        } else {
            localization.add(formSection.titleProperty(), titleLocaleKey);
        }

        if(descriptionLocaleKey == null) {
            formSection.setDescription(description);
        } else {
            localization.add(formSection.descriptionProperty(), descriptionLocaleKey);
        }

        metadata.forEach((k, v) -> {
            if (v instanceof String) {
                KeyValue<String> keyValue = beanManager.create(AbstractKeyValueBean.class);
                keyValue.setKey(k);
                keyValue.setValue((String) v);
                formSection.getLayoutMetadata().add(keyValue);
            } else if (v instanceof Boolean) {
                KeyValue<Boolean> keyValue = beanManager.create(AbstractKeyValueBean.class);
                keyValue.setKey(k);
                keyValue.setValue((Boolean) v);
                formSection.getLayoutMetadata().add(keyValue);
            }
        });

        if (iconCode != null) {
            Icon icon = beanManager.create(IconBean.class);
            icon.setIconFamily(iconFamily);
            icon.setIconCode(iconCode);
            formSection.setIcon(icon);
        }

        formSection.getFields().addAll(fields);

        return formSection;
    }

    public static FormSectionBuilder<FormSectionBean> create(BeanManager beanManager) {
        return new FormSectionBuilder<>(FormSectionBean.class, beanManager);
    }

    public static FormSectionBuilder<FormSectionBean> create(BeanManager beanManager, BeanLocalization localization) {
        return new FormSectionBuilder<>(FormSectionBean.class, beanManager, localization);
    }
}
