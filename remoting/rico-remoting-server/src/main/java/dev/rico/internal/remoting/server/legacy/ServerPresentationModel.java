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
package dev.rico.internal.remoting.server.legacy;

import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.remoting.legacy.LegacyConstants;
import dev.rico.internal.remoting.legacy.core.BasePresentationModel;
import org.apiguardian.api.API;

import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ServerPresentationModel extends BasePresentationModel<ServerAttribute> {

    private static final Logger LOG = LoggerFactory.getLogger(ServerPresentationModel.class);

    private ServerModelStore modelStore;

    /**
     * @param id if id is null or empty, an auto-generated id will be used
     */
    public ServerPresentationModel(final String id, final List<ServerAttribute> attributes, final ServerModelStore serverModelStore) {
        super((id != null && id.length() > 0) ? id : makeId(serverModelStore), attributes);
        if (id != null && id.endsWith(LegacyConstants.SERVER_PM_AUTO_ID_SUFFIX)) {
            LOG.trace("Creating a PM with self-provided id '{}' even though it ends with a reserved suffix.", id);
        }
        modelStore = serverModelStore;
    }

    private static String makeId(final ServerModelStore serverModelStore) {
        long newId = serverModelStore.pmInstanceCount++;
        return newId + LegacyConstants.SERVER_PM_AUTO_ID_SUFFIX;
    }

    public ServerModelStore getModelStore() {
        return modelStore;
    }

    public void setModelStore(final ServerModelStore modelStore) {
        this.modelStore = modelStore;
    }
}
