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
package dev.rico.internal.remoting.repo;

import dev.rico.core.functional.Subscription;
import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.RemotingUtils;
import dev.rico.internal.remoting.UpdateSource;
import dev.rico.remoting.converter.BeanRepo;
import org.apiguardian.api.API;

import java.util.*;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class BeanRepository implements BeanRepo {

    private final Map<Object, String> objectPmToRemotingPm = new IdentityHashMap<>();
    private final Map<String, Object> remotingIdToObjectPm = new HashMap<>();

    /**
     * For unit tests
     * @param bean
     * @return
     */
    public boolean isManaged(Object bean) {
        RemotingUtils.assertIsRemotingBean(bean);
        return objectPmToRemotingPm.containsKey(bean);
    }

    public <T> void delete(T bean) {
        RemotingUtils.assertIsRemotingBean(bean);
        String id = objectPmToRemotingPm.remove(bean);
        remotingIdToObjectPm.remove(id);
    }

    public Object getBean(String sourceId) {
        if(sourceId == null) {
            return null;
        }
        if(!remotingIdToObjectPm.containsKey(sourceId)) {
            throw new IllegalArgumentException("No bean instance found with id " + sourceId);
        }
        return remotingIdToObjectPm.get(sourceId);
    }

    public String getRemotingId(Object bean) {
        if (bean == null) {
            return null;
        }
        RemotingUtils.assertIsRemotingBean(bean);
        try {
            return objectPmToRemotingPm.get(bean);
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("Only managed remoting beans can be used.", ex);
        }
    }

    public void registerBean(String id, Object bean, UpdateSource source) {
        Assert.requireNonBlank(id, "id");
        Assert.requireNonNull(bean, "bean");
        Assert.requireNonNull(source, "source");

        RemotingUtils.assertIsRemotingBean(bean);
        remotingIdToObjectPm.put(id, bean);
        objectPmToRemotingPm.put(bean, id);
    }
}
