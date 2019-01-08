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
package dev.rico.integrationtests.server.property;

import dev.rico.integrationtests.property.ObservableListBean;
import dev.rico.server.remoting.Param;
import dev.rico.server.remoting.RemotingAction;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;

import java.util.UUID;

import static dev.rico.integrationtests.property.PropertyTestConstants.ADD_ID_ACTION;
import static dev.rico.integrationtests.property.PropertyTestConstants.CHECK_SIZE_ACTION;
import static dev.rico.integrationtests.property.PropertyTestConstants.INDEX_PARAM;
import static dev.rico.integrationtests.property.PropertyTestConstants.LIST_CONTROLLER_NAME;
import static dev.rico.integrationtests.property.PropertyTestConstants.REMOVE_ACTION;
import static dev.rico.integrationtests.property.PropertyTestConstants.SIZE_PARAM;

@RemotingController(LIST_CONTROLLER_NAME)
public class ObservableListController {

    @RemotingModel
    private ObservableListBean model;

    @RemotingAction(ADD_ID_ACTION)
    public void addId() {
        model.getList().add(UUID.randomUUID().toString());
    }

    @RemotingAction(REMOVE_ACTION)
    public void remove(@Param(INDEX_PARAM) final int index) {
        model.getList().remove(index);
    }

    @RemotingAction(CHECK_SIZE_ACTION)
    public void checkSize(@Param(SIZE_PARAM) final int size) {
        if (model.getList().size() == size) {
            model.setCheckResult(true);
        } else {
            model.setCheckResult(false);
        }
    }

}
