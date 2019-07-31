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

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apiguardian.api.API.Status.DEPRECATED;


/**
 * Contains four values which are used in initialization of a ModelStore.  These values specify the
 * initial capacity of the four maps maintained by the ModelStore:
 * <ol><li>pmCapacity: map of presentation models (key: unique PM ID); default is 1024</li>
 * <li>typeCapacity: map of presentation model types(key: PM type, returns a list of all PM's with that type); default 64</li>
 * <li>attributeCapacity: map of attributes (key: unique attribute ID); default is 4096</li>
 * <li>qualifierCapacity: map of attributes (key: qualifier, returns a list of all attributes with that qualifier); default 1024</li></ol>
 */
@API(since = "0.x", status = DEPRECATED)
@Deprecated
public class ModelStoreConfig {

    private static final Logger LOG  = LoggerFactory.getLogger(ModelStoreConfig.class);

    private int pmCapacity;
    private int typeCapacity;
    private int attributeCapacity;
    private int qualifierCapacity;

    public ModelStoreConfig() {
        setPmCapacity(1024);
        setTypeCapacity(64);
        setAttributeCapacity(1024 * 4);
        setQualifierCapacity(1024);
    }

    public int getPmCapacity() {
        return pmCapacity;
    }

    public void setPmCapacity(final int pmCapacity) {
        ensurePowerOfTwo("pmCapacity", pmCapacity);
        this.pmCapacity = pmCapacity;
    }

    public int getTypeCapacity() {
        return typeCapacity;
    }

    public void setTypeCapacity(final int typeCapacity) {
        ensurePowerOfTwo("typeCapacity", typeCapacity);
        this.typeCapacity = typeCapacity;
    }

    public int getAttributeCapacity() {
        return attributeCapacity;
    }

    public void setAttributeCapacity(final int attributeCapacity) {
        ensurePowerOfTwo("attributeCapacity", attributeCapacity);
        this.attributeCapacity = attributeCapacity;
    }

    public int getQualifierCapacity() {
        return qualifierCapacity;
    }

    public void setQualifierCapacity(final int qualifierCapacity) {
        ensurePowerOfTwo("qualifierCapacity", qualifierCapacity);
        this.qualifierCapacity = qualifierCapacity;
    }

    // all the capacities will be used to initialize HashMaps so they should be powers of two
    private void ensurePowerOfTwo(final String parameter, final int number) {
        if (Integer.bitCount(number) > 1) {
            LOG.warn("Parameter {} should be power of two but was {}", parameter, number);
        }
    }
}

