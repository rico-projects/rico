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
package dev.rico.internal.server.crud;

import dev.rico.core.functional.Subscription;
import dev.rico.internal.core.Assert;
import dev.rico.server.data.AbstractEntity;
import dev.rico.server.data.EntityService;
import dev.rico.internal.server.data.event.PersistenceListener;
import dev.rico.internal.server.data.event.PersistenceContextImpl;
import dev.rico.internal.server.data.mapping.BeanConverter;
import dev.rico.remoting.BeanManager;
import dev.rico.server.remoting.ClientSessionExecutor;
import dev.rico.server.remoting.RemotingContext;

import javax.annotation.PostConstruct;

@SuppressWarnings("unchecked")
public abstract class AbstractJpaCrudController<B, E extends AbstractEntity> extends AbstractCrudController<Long, B, E> {

    private final ClientSessionExecutor clientSessionExecutor;

    private final Subscription listenerSubscription;

    protected AbstractJpaCrudController(final Class modelClass, final Class entityClass, final BeanManager manager, final EntityService<E> crudService, final BeanConverter<Long, B, E> converter, final RemotingContext remotingContext) {
        super(modelClass, entityClass, manager, crudService, converter, Assert.requireNonNull(remotingContext, "remotingContext").getEventBus());
        this.clientSessionExecutor = remotingContext.createSessionExecutor();
        final PersistenceListener persistenceListener = new PersistenceListener() {
            @Override
            public void onEntityPersisted(final AbstractEntity entity) {}

            @Override
            public void onEntityRemoved(final AbstractEntity entity) {
                Assert.requireNonNull(entity, "entity");

                final E currentEntity = getEntity();
                if(currentEntity != null && currentEntity.equals(entity) && !remotingContext.isActive()) {
                    clientSessionExecutor.runLaterInClientSession(() -> onEntityRemovedConflict());
                }
            }

            @Override
            public void onEntityUpdated(final AbstractEntity entity) {
                Assert.requireNonNull(entity, "entity");

                final E currentEntity = getEntity();
                if(currentEntity != null && currentEntity.equals(entity) && !remotingContext.isActive()) {
                    clientSessionExecutor.runLaterInClientSession(() -> onEntityUpdatedConflict());
                }
            }
        };
        listenerSubscription = PersistenceContextImpl.getInstance().addListener(persistenceListener);
    }

    @PostConstruct
    protected void onDestroy() {
        super.onDestroy();
        listenerSubscription.unsubscribe();
    }

    protected void onEntityRemovedConflict() {}

    protected void onEntityUpdatedConflict() {}
}
