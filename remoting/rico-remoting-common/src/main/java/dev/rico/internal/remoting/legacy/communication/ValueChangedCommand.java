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
package dev.rico.internal.remoting.legacy.communication;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.DEPRECATED;

@API(since = "0.x", status = DEPRECATED)
public final class ValueChangedCommand extends Command {

    private String attributeId;

    private Object newValue;

    public ValueChangedCommand() {
        super(CommandConstants.VALUE_CHANGED_COMMAND_ID);
    }

    public ValueChangedCommand(final String attributeId, final Object newValue) {
        this();
        this.attributeId = attributeId;
        this.newValue = newValue;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(final String attributeId) {
        this.attributeId = attributeId;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(final Object newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return super.toString() + " attr:" + attributeId + " -> " + String.valueOf(newValue);
    }

}
