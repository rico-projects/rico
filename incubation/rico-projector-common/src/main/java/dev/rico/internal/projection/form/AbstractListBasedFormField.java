package dev.rico.internal.projection.form;

import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public abstract class AbstractListBasedFormField<V> extends AbstractFormFieldBean<V> implements ListBasedFormField<V>  {

    private Property<Boolean> multiSelect;

    private Property<Boolean> containsNullValue;

    @Override
    public Property<Boolean> multiSelectProperty() {
        return multiSelect;
    }

    @Override
    public Property<Boolean> containsNullValueProperty() {
        return containsNullValue;
    }
}
