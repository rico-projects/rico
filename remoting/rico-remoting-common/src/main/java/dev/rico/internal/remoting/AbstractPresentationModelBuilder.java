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

import dev.rico.internal.remoting.legacy.core.PresentationModel;
import org.apiguardian.api.API;

import java.util.UUID;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public abstract class AbstractPresentationModelBuilder<T extends PresentationModel> implements PresentationModelBuilder<T> {

    private String type;

    private String id;

    public AbstractPresentationModelBuilder() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public PresentationModelBuilder<T> withType(final String type) {
        this.type = type;
        return this;
    }

    @Override
    public PresentationModelBuilder<T> withId(final String id) {
        this.id = id;
        return this;
    }

    protected String getType() {
        return type;
    }

    protected void setType(final String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }
}
