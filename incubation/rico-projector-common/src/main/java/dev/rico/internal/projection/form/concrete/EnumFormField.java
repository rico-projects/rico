package dev.rico.internal.projection.form.concrete;

import dev.rico.internal.core.ReflectionHelper;
import dev.rico.internal.projection.form.AbstractListBasedFormField;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;

public class EnumFormField<V extends Enum> extends AbstractListBasedFormField<V> {

    private ObservableList<V> items;

    private ObservableList<V> selectedValues;

    private Property<V> selectedValue;

    private Property<Class<V>> contentType;

    public void init(Class<V> enumClass) {
        getItems().addAll(ReflectionHelper.getAllValues(enumClass));
        setContentType(enumClass);
    }

    @Override
    public ObservableList<V> getItems() {
        return items;
    }

    @Override
    public ObservableList<V> getSelectedValues() {
        return selectedValues;
    }

    @Override
    public Property<V> selectedValueProperty() {
        return selectedValue;
    }

    @Override
    public Property<Class<V>> contentTypeProperty() {
        return contentType;
    }

    @Override
    public Property<V> valueProperty() {
        return selectedValue;
    }
}
