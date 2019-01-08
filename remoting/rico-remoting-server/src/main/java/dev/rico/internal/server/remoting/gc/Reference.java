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
package dev.rico.internal.server.remoting.gc;

import dev.rico.remoting.RemotingBean;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Describes a direct reference between 2 remoting beans (see {@link RemotingBean}). In each
 * reference one remoting bean must be the parent that holds the reference to the other remoting bean that is defined as child.
 * Internally the remoting beans are defined by {@link Instance} instances that hold additional informationen next to the bean instance.
 */
@API(since = "0.x", status = INTERNAL)
public abstract class Reference {

    private Instance parent;

    private Instance child;

    /**
     * Constructor
     * @param parent the parent remoting bean
     * @param child the child remoting bean
     */
    public Reference(Instance parent, Instance child) {
        this.parent = parent;
        this.child = child;
    }

    /**
     * Returns the parent remoting bean
     * @return the parent remoting bean
     */
    public Instance getParent() {
        return parent;
    }

    /**
     * Returns true if this reference is part of a circular reference.
     * @return true if this reference is part of a circular reference.
     */
    public boolean hasCircularReference() {
        return recursiveCircularReferenceCheck(parent);
    }

    private boolean recursiveCircularReferenceCheck(Instance currentInstance) {
        if(currentInstance == this.child) {
            return true;
        }
        for(Reference reference : currentInstance.getReferences()) {
            if(recursiveCircularReferenceCheck(reference.getParent())) {
                return true;
            }
        }
        return false;
    }
}
