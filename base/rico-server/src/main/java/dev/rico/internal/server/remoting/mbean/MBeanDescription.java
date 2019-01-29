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
package dev.rico.internal.server.remoting.mbean;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * A general description that can be used to define how a MBean will be registered
 */
@API(since = "0.x", status = INTERNAL)
public class MBeanDescription {

    private String domainName;

    private String name;

    private String type;

    /**
     * Constructor
     * @param domainName the domain name
     * @param name the name
     * @param type the type
     */
    public MBeanDescription(final String domainName, final String name, final String type) {
        this.domainName = domainName;
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMBeanName(final String uniqueIdentifier) {
        return domainName + ":00=" + type + ",name=" + name + "-" + uniqueIdentifier;
    }

}
