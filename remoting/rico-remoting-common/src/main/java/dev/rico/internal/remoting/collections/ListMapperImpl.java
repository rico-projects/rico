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
package dev.rico.internal.remoting.collections;

import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.ClassRepository;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.remoting.ListMapper;
import dev.rico.internal.remoting.MappingException;
import dev.rico.internal.remoting.PresentationModelBuilder;
import dev.rico.internal.remoting.PresentationModelBuilderFactory;
import dev.rico.internal.remoting.RemotingConstants;
import dev.rico.internal.remoting.RemotingEventHandler;
import dev.rico.internal.remoting.info.ClassInfo;
import dev.rico.internal.remoting.info.PropertyInfo;
import dev.rico.internal.remoting.legacy.core.ModelStore;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import dev.rico.remoting.ListChangeEvent;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ListMapperImpl implements ListMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ListMapperImpl.class);

    private final BeanRepository beanRepository;
    private final ClassRepository classRepository;
    protected final PresentationModelBuilderFactory builderFactory;

    public ListMapperImpl(final ModelStore modelStore, final ClassRepository classRepository, final BeanRepository beanRepository, final PresentationModelBuilderFactory builderFactory, final EventDispatcher dispatcher) {
        this.beanRepository = beanRepository;
        this.classRepository = classRepository;
        this.builderFactory = builderFactory;

        dispatcher.addListSpliceHandler(new RemotingEventHandler() {
            @Override
            @SuppressWarnings("unchecked")
            public void onEvent(PresentationModel model) {
                try {
                    final String sourceId = model.getAttribute("source").getValue().toString();
                    final String attributeName = model.getAttribute("attribute").getValue().toString();

                    final Object bean = ListMapperImpl.this.beanRepository.getBean(sourceId);
                    final ClassInfo classInfo = ListMapperImpl.this.classRepository.getOrCreateClassInfo(bean.getClass());
                    final PropertyInfo observableListInfo = classInfo.getObservableListInfo(attributeName);

                    final ObservableArrayList list = (ObservableArrayList) observableListInfo.getPrivileged(bean);

                    final int from = ((Number) model.getAttribute("from").getValue()).intValue();
                    final int to = ((Number) model.getAttribute("to").getValue()).intValue();
                    final int count = ((Number) model.getAttribute("count").getValue()).intValue();

                    final List<Object> newElements = new ArrayList<>(count);
                    for (int i = 0; i < count; i++) {
                        final Object remotingValue = model.getAttribute(Integer.toString(i)).getValue();
                        final Object value = observableListInfo.convertFromRemoting(remotingValue);
                        newElements.add(value);
                    }

                    list.internalSplice(from, to, newElements);
                } catch (final Exception ex) {
                    //TODO: This exception must be handled!
                    LOG.error("Invalid LIST_SPLICE command received: " + model, ex);
                } finally {
                    if (model != null) {
                        modelStore.remove(model);
                    }
                }

            }
        });
    }

    @Override
    public void processEvent(final PropertyInfo observableListInfo, final String sourceId, final ListChangeEvent<?> event) {
        final String attributeName = observableListInfo.getAttributeName();

        for (final ListChangeEvent.Change<?> change : event.getChanges()) {

            final int from = change.getFrom();
            final int to = from + change.getRemovedElements().size();
            final List<?> newElements = event.getSource().subList(from, change.getTo());
            final int count = newElements.size();

            final PresentationModelBuilder builder = builderFactory.createBuilder();
            builder.withType(RemotingConstants.LIST_SPLICE)
                    .withAttribute("source", sourceId)
                    .withAttribute("attribute", attributeName)
                    .withAttribute("from", from)
                    .withAttribute("to", to)
                    .withAttribute("count", count);

            int i = 0;
            for (final Object current : newElements) {
                try {
                    builder.withAttribute(Integer.toString(i++), observableListInfo.convertToRemoting(current));
                } catch (final Exception e) {
                    throw new MappingException("Error in event processing!", e);
                }
            }

            builder.create();
        }
    }
}
