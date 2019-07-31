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
package dev.rico.internal.remoting.legacy.core;

import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apiguardian.api.API.Status.DEPRECATED;

/**
 * A central data structure to store presentation models and their attributes
 * both on the client (view) and on the server (controller) side for separate access.
 * <p/>A model store maintains a set of presentation models and their attributes.  Presentation models may be added to
 * or removed from the model store; the attributes of such presentation models are automatically also added or removed.
 * <p/>
 * It provides methods to:
 * <ol>
 *     <li>obtain a list of all (unique) presentation model ID's;</li>
 *     <li>obtain a list of all presentation models;</li>
 *     <li>locate individual models by unique ID;</li>
 *     <li>obtain a list of all models of a given type;</li>
 *     <li>locate individual attributes by unique ID; and</li>
 *     <li>obtain a list of all attributes with a given qualifier.</li>
 * </ol>
 * In addition, the model store provides methods to listen for changes to the model store.
 */
@API(since = "0.x", status = DEPRECATED)
@Deprecated
public class ModelStore<A extends BaseAttribute, P extends BasePresentationModel<A>> {

    // We maintain four indexes in this data structure in order to efficiently access
    // - presentation models: by id; by type
    // - attributes: by id; by qualifier

    private final Map<String, P>        presentationModels;
    private final Map<String, List<P>>  modelsPerType;
    private final Map<String, A>        attributesPerId;
    private final Map<String, List<A>>  attributesPerQualifier;

    private final Set<ModelStoreListenerWrapper<A, P>> modelStoreListeners = new LinkedHashSet<ModelStoreListenerWrapper<A, P>>();

    private final PropertyChangeListener ATTRIBUTE_WORKER = new PropertyChangeListener() {
        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            A attribute = (A) event.getSource();
            String oldQualifier = (String) event.getOldValue();
            String newQualifier = (String) event.getNewValue();

            if (null != oldQualifier) removeAttributeByQualifier(attribute, oldQualifier);
            if (null != newQualifier) addAttributeByQualifier(attribute);
        }
    };

    /**
     * Constructs a model store with default capacities.
     * @see ModelStoreConfig
     */
    public ModelStore() {
        this(new ModelStoreConfig());
    }

    /**
     * Constructs a model store using the supplied configuration to specify default capacities.
     * @param config specifies the default capacities for the model store
     * @see ModelStoreConfig
     */
    public ModelStore(final ModelStoreConfig config) {
        presentationModels      = new HashMap<String, P>        (config.getPmCapacity());
        modelsPerType           = new HashMap<String, List<P>>  (config.getTypeCapacity());
        attributesPerId         = new HashMap<String, A>        (config.getAttributeCapacity());
        attributesPerQualifier  = new HashMap<String, List<A>>  (config.getQualifierCapacity());
    }

    /**
     * Returns a {@code Set} of all known presentation model ids.<br/>
     * Never returns null. The returned {@code Set} is immutable.
     *
     * @return a {@code} Set of all ids of all presentation models contained in this store.
     */
    public Set<String> listPresentationModelIds() {
        return Collections.unmodifiableSet(presentationModels.keySet());
    }

    /**
     * Returns a {@code Collection} of all presentation models found in this store.<br/>
     * Never returns {@code null}. The returned {@code Collection} is immutable.
     *
     * @return a {@code Collection} of all presentation models found in this store.
     */
    public Collection<P> listPresentationModels() {
        return Collections.unmodifiableCollection(presentationModels.values());
    }

    /**
     * Adds a presentation model to this store.<br/>
     * Presentation model ids should be unique. This method guarantees this condition by disallowing
     * models with duplicate ids to be added.
     *
     * @param model the model to be added.
     * @return if the add operation was successful or not.
     * @throws IllegalArgumentException if a presentation model with the model's ID is already in the model store.
     */
    public boolean add(final P model) {
        if (null == model) return false;

        if (presentationModels.containsKey(model.getId())) {
            throw new IllegalArgumentException("There already is a PM with id " + model.getId());
        }
        boolean added = false;
        if (!presentationModels.containsValue(model)) {
            presentationModels.put(model.getId(), model);
            addPresentationModelByType(model);
            for (A attribute : model.getAttributes()) {
                addAttributeById(attribute);
                attribute.addPropertyChangeListener(BaseAttribute.QUALIFIER_NAME, ATTRIBUTE_WORKER);
                if (!Assert.isBlank(attribute.getQualifier())) addAttributeByQualifier(attribute);
            }
            fireModelStoreChangedEvent(model, ModelStoreEvent.Type.ADDED);
            added = true;
        }
        return added;
    }

    /**
     * Removes a presentation model from this store.<br/>
     *
     * @param model the model to be removed from the store.
     * @return if the removePresentationModel operation was successful or not.
     */
    public boolean remove(final P model) {
        if (null == model) return false;
        boolean removed = false;
        if (presentationModels.containsValue(model)) {
            removePresentationModelByType(model);
            presentationModels.remove(model.getId());
            for (final A attribute : model.getAttributes()) {
                removeAttributeById(attribute);
                removeAttributeByQualifier(attribute);
                attribute.removePropertyChangeListener(BaseAttribute.QUALIFIER_NAME, ATTRIBUTE_WORKER);
            }
            fireModelStoreChangedEvent(model, ModelStoreEvent.Type.REMOVED);
            removed = true;
        }
        return removed;
    }

    protected void addAttributeById(final A attribute) {
        if (null == attribute || attributesPerId.containsKey(attribute.getId())) return;
        attributesPerId.put(attribute.getId(), attribute);
    }

    protected void removeAttributeById(final A attribute) {
        if (null == attribute) return;
        attributesPerId.remove(attribute.getId());
    }

    protected void addAttributeByQualifier(final A attribute) {
        if (null == attribute) return;
        final String qualifier = attribute.getQualifier();
        if (Assert.isBlank(qualifier)) return;
        List<A> list = attributesPerQualifier.get(qualifier);
        if (null == list) {
            list = new ArrayList<A>();
            attributesPerQualifier.put(qualifier, list);
        }
        if (!list.contains(attribute)) list.add(attribute);
    }

    protected void removeAttributeByQualifier(final A attribute) {
        if (null == attribute) return;
        final String qualifier = attribute.getQualifier();
        if (Assert.isBlank(qualifier)) return;
        final List<A> list = attributesPerQualifier.get(qualifier);
        if (null != list) {
            list.remove(attribute);
        }
    }

    protected void addPresentationModelByType(final P model) {
        if (null == model) return;
        final String type = model.getPresentationModelType();
        if (Assert.isBlank(type)) return;
        List<P> list = modelsPerType.get(type);
        if (null == list) {
            list = new ArrayList<P>();
            modelsPerType.put(type, list);
        }
        if (!list.contains(model)) list.add(model);
    }

    protected void removePresentationModelByType(final P model) {
        if (null == model) return;
        final String type = model.getPresentationModelType();
        if (Assert.isBlank(type)) return;
        final List<P> list = modelsPerType.get(type);
        if (null == list) return;
        list.remove(model);
        if (list.isEmpty()) {
            modelsPerType.remove(type);
        }
    }

    protected void removeAttributeByQualifier(final A attribute, final String qualifier) {
        if (Assert.isBlank(qualifier)) return;
        final List<A> list = attributesPerQualifier.get(qualifier);
        if (null == list) return;
        list.remove(attribute);
        if (list.isEmpty()) {
            attributesPerQualifier.remove(qualifier);
        }
    }

    /**
     * Find a presentation model by the given id.<br/>
     * <strong>WARNING:</strong> this method will return {@code null} if no match is found.
     *
     * @param id the id to search
     * @return the presentation model with the specified ID, otherwise {@code null}.
     */
    public P findPresentationModelById(final String id) {
        return presentationModels.get(id);
    }

    /**
     * Finds all presentation models that share the same type.<br/>
     * The returned {@code List} is never null (though it may be empty), and is immutable.
     *
     * @param type the type to search for
     * @return a {@code List} of all presentation models with the specified type.
     */
    public List<P> findAllPresentationModelsByType(final String type) {
        if (Assert.isBlank(type) || !modelsPerType.containsKey(type)) return Collections.emptyList();
        return Collections.unmodifiableList(modelsPerType.get(type));
    }

    /**
     * Determines if a presentation model with the specified ID is contained in this model store.
     *
     * @param id the id to search in the store.
     * @return true if the model is found in this store, otherwise false.
     */
    public boolean containsPresentationModel(final String id) {
        return presentationModels.containsKey(id);
    }

    /**
     * Finds an attribute by its id.<br/>
     * <strong>WARNING:</strong> this method will return {@code null} if no match is found.
     *
     * @param id the id to search for.
     * @return the attribute with the specified ID, otherwise {@code null}.
     */
    public A findAttributeById(final String id) {
        return attributesPerId.get(id);
    }

    /**
     * Returns a {@code List} of all attributes that share the same qualifier.<br/>
     * Never returns null, but may return an empty list. The returned {@code List} is immutable.
     *
     * @return a {@code List} of all attributes with the specified qualifier.
     */
    public List<A> findAllAttributesByQualifier(final String qualifier) {
        if (Assert.isBlank(qualifier) || !attributesPerQualifier.containsKey(qualifier)) return Collections.emptyList();
        return Collections.unmodifiableList(attributesPerQualifier.get(qualifier));
    }

    /**
     * Adds the specified attribute to the model store.
     * <p/>Note: attributes belonging to a given presentation model are automatically added to the model store
     * when the presentation model is added.
     * @param attribute attribute to be added to the model store
     * @see #add(BasePresentationModel)
     */
    @Deprecated
    public void registerAttribute(final A attribute) {
        if (null == attribute) return;
        boolean listeningAlready = false;
        for (PropertyChangeListener listener : attribute.getPropertyChangeListeners(BaseAttribute.QUALIFIER_NAME)) {
            if (ATTRIBUTE_WORKER == listener) {
                listeningAlready = true;
                break;
            }
        }

        if (!listeningAlready) {
            attribute.addPropertyChangeListener(BaseAttribute.QUALIFIER_NAME, ATTRIBUTE_WORKER);
        }

        addAttributeByQualifier(attribute);
        addAttributeById(attribute);
    }

    public void addModelStoreListener(final ModelStoreListener<A, P> listener) {
        addModelStoreListener(null, listener);
    }

    public void addModelStoreListener(final String presentationModelType, final ModelStoreListener<A, P> listener) {
        if (null == listener) return;
        final ModelStoreListenerWrapper<A, P> wrapper = new ModelStoreListenerWrapper<A, P>(presentationModelType, listener);
        if (!modelStoreListeners.contains(wrapper)) modelStoreListeners.add(wrapper);
    }

    public void removeModelStoreListener(final ModelStoreListener<A, P> listener) {
        removeModelStoreListener(null, listener);
    }

    public void removeModelStoreListener(final String presentationModelType, final ModelStoreListener<A, P> listener) {
        if (null == listener) return;
        modelStoreListeners.remove(new ModelStoreListenerWrapper<A, P>(presentationModelType, listener));
    }

    public boolean hasModelStoreListener(final ModelStoreListener<A, P> listener) {
        return hasModelStoreListener(null, listener);
    }

    public boolean hasModelStoreListener(final String presentationModelType, final ModelStoreListener<A, P> listener) {
        return null != listener &&
                modelStoreListeners.contains(new ModelStoreListenerWrapper<A, P>(presentationModelType, listener));
    }

    protected void fireModelStoreChangedEvent(final P model, final ModelStoreEvent.Type eventType) {
        if (modelStoreListeners.isEmpty()) return;
        final ModelStoreEvent<A, P> event = new ModelStoreEvent<A, P>(eventType, model);
        for (ModelStoreListener<A, P> listener : modelStoreListeners) {
            listener.modelStoreChanged(event);
        }
    }

    public void updateQualifiers(final P presentationModel) {
        for (final A source : presentationModel.getAttributes()) {
            if (null == source.getQualifier()) continue;
            for (final A target : findAllAttributesByQualifier(source.getQualifier())) {
                target.setValue(source.getValue());
            }
        }
    }
}
