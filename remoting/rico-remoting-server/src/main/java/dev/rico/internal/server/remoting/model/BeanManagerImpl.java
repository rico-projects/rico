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
package dev.rico.internal.server.remoting.model;

import dev.rico.internal.remoting.RemotingUtils;
import dev.rico.internal.remoting.repo.Repository;
import dev.rico.server.remoting.BeanManager;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class BeanManagerImpl implements BeanManager {

    protected final ServerRepository repository;

    public BeanManagerImpl(final ServerRepository repository) {
        this.repository = Assert.requireNonNull(repository, "repository");
    }

    @Override
    public <T> T create(final Class<T> beanClass) {
        RemotingUtils.assertIsRemotingBean(beanClass);
        try {
            return repository.createSubModel(beanClass);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating remoting bean", e);
        }
    }
}
