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
package dev.rico.internal.remoting;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.legacy.LegacyConstants;
import dev.rico.internal.remoting.legacy.core.ModelStore;
import dev.rico.internal.remoting.legacy.core.ModelStoreEvent;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public abstract class EventDispatcherImpl implements EventDispatcher {

    private final List<RemotingEventHandler> modelAddedHandlers = new ArrayList<>(1);
    private final List<RemotingEventHandler> modelRemovedHandlers = new ArrayList<>(1);
    private final List<RemotingEventHandler> listSpliceHandlers = new ArrayList<>(1);
    private final List<RemotingEventHandler> controllerActionCallBeanAddedHandlers = new ArrayList<>(1);
    private final List<RemotingEventHandler> controllerActionCallBeanRemovedHandlers = new ArrayList<>(1);
    private final List<RemotingEventHandler> internalAttributesBeanAddedHandlers = new ArrayList<>(1);

    public EventDispatcherImpl(final ModelStore modelStore) {
        Assert.requireNonNull(modelStore, "modelStore").addModelStoreListener(this);
    }

    @Override
    public void addAddedHandler(RemotingEventHandler handler) {
        modelAddedHandlers.add(handler);
    }

    @Override
    public void addRemovedHandler(RemotingEventHandler handler) {
        modelRemovedHandlers.add(handler);
    }

    @Override
    public void addListSpliceHandler(RemotingEventHandler handler) {
        listSpliceHandlers.add(handler);
    }

    @Override
    public void addControllerActionCallBeanAddedHandler(RemotingEventHandler handler) {
        controllerActionCallBeanAddedHandlers.add(handler);
    }

    @Override
    public void addControllerActionCallBeanRemovedHandler(RemotingEventHandler handler) {
        controllerActionCallBeanRemovedHandlers.add(handler);
    }

    @Override
    public void onceInternalAttributesBeanAddedHandler(RemotingEventHandler handler) {
        internalAttributesBeanAddedHandlers.add(handler);
    }

    @Override
    public void modelStoreChanged(ModelStoreEvent event) {
        Assert.requireNonNull(event, "event");
        final PresentationModel model = event.getPresentationModel();
        if (!isLocalChange(model)) {
            if (ModelStoreEvent.Type.ADDED == event.getType()) {
                onAddedHandler(model);
            } else if (ModelStoreEvent.Type.REMOVED == event.getType()) {
                onRemovedHandler(model);
            }
        }
    }

    private void onAddedHandler(PresentationModel model) {
        Assert.requireNonNull(model, "model");
        final String type = model.getPresentationModelType();
        switch (type) {
            case RemotingConstants.REMOTING_BEAN:
                // ignore
                break;
            case RemotingConstants.CONTROLLER_ACTION_CALL_BEAN_NAME:
                for (final RemotingEventHandler handler : controllerActionCallBeanAddedHandlers) {
                    handler.onEvent(model);
                }
                break;
            case RemotingConstants.INTERNAL_ATTRIBUTES_BEAN_NAME:
                for (final RemotingEventHandler handler : internalAttributesBeanAddedHandlers) {
                    handler.onEvent(model);
                }
                internalAttributesBeanAddedHandlers.clear();
                break;
            case RemotingConstants.LIST_SPLICE:
                for (final RemotingEventHandler handler : listSpliceHandlers) {
                    handler.onEvent(model);
                }
                break;
            default:
                for (final RemotingEventHandler handler : modelAddedHandlers) {
                    handler.onEvent(model);
                }
                break;
        }
    }

    private void onRemovedHandler(PresentationModel model) {
        Assert.requireNonNull(model, "model");
        final String type = model.getPresentationModelType();
        switch (type) {
            case RemotingConstants.REMOTING_BEAN:
            case RemotingConstants.LIST_SPLICE:
            case RemotingConstants.INTERNAL_ATTRIBUTES_BEAN_NAME:
                // ignore
                break;
            case RemotingConstants.CONTROLLER_ACTION_CALL_BEAN_NAME:
                for (final RemotingEventHandler handler : controllerActionCallBeanRemovedHandlers) {
                    handler.onEvent(model);
                }
                break;
            default:
                for (final RemotingEventHandler handler : modelRemovedHandlers) {
                    handler.onEvent(model);
                }
                break;
        }
    }

    private boolean isLocalChange(PresentationModel model) {
        Assert.requireNonNull(model, "model");
        final Object value = model.getAttribute(LegacyConstants.SOURCE_SYSTEM).getValue();
        return getLocalSystemIdentifier().equals(value);
    }

    public abstract String getLocalSystemIdentifier();
}
