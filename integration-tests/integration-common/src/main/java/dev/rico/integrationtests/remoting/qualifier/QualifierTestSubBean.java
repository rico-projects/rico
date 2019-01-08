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
package dev.rico.integrationtests.remoting.qualifier;


import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.Property;

@RemotingBean
public class QualifierTestSubBean {

    private Property<Boolean> booleanValue;

    private Property<Integer> integerValue;

    private Property<String> stringValue;

    public Boolean getBooleanValue() {
        return booleanValue.get();
    }

    public Property<Boolean> booleanValueProperty() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue.set(booleanValue);
    }

    public Integer getIntegerValue() {
        return integerValue.get();
    }

    public Property<Integer> integerValueProperty() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue.set(integerValue);
    }

    public String getStringValue() {
        return stringValue.get();
    }

    public Property<String> stringValueProperty() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue.set(stringValue);
    }
}

