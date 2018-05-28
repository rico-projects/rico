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
package dev.rico.internal.remoting.legacy.communication;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.DEPRECATED;

@Deprecated
@API(since = "0.x", status = DEPRECATED)
public final class PresentationModelDeletedCommand extends Command {

    public PresentationModelDeletedCommand(String pmId) {
        this();
        this.pmId = pmId;
    }

    public PresentationModelDeletedCommand() {
        super(CommandConstants.PRESENTATION_MODEL_DELETED_COMMAND_ID);
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(final String pmId) {
        this.pmId = pmId;
    }

    @Override
    public String toString() {
        return super.toString() + " pmId " + pmId;
    }

    private String pmId;
}
