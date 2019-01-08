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
package dev.rico.integrationtests.server.remoting.runlater;

import dev.rico.integrationtests.remoting.runlater.RunLaterTestBean;
import dev.rico.server.remoting.RemotingModel;
import dev.rico.server.remoting.ClientSessionExecutor;
import dev.rico.server.remoting.RemotingAction;
import dev.rico.server.remoting.RemotingContext;
import dev.rico.server.remoting.RemotingController;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.rico.integrationtests.remoting.runlater.RunLaterTestConstants.RUN_LATER_ACTION_NAME;
import static dev.rico.integrationtests.remoting.runlater.RunLaterTestConstants.RUN_LATER_ASYNC_ACTION_NAME;
import static dev.rico.integrationtests.remoting.runlater.RunLaterTestConstants.RUN_LATER_CONTROLLER_NAME;

@RemotingController(RUN_LATER_CONTROLLER_NAME)
public class RunLaterTestController {

    private final AtomicInteger callIndex = new AtomicInteger();

    private final ClientSessionExecutor sessionExecutor;

    @RemotingModel
    private RunLaterTestBean model;

    @Inject
    public RunLaterTestController(final RemotingContext remotingContext) {
        this.sessionExecutor = remotingContext.createSessionExecutor();
    }

    @PostConstruct
    public void init() {
        resetModel();

        model.setPostConstructPreRunLaterCallIndex(callIndex.incrementAndGet());
        sessionExecutor.runLaterInClientSession(() -> model.setPostConstructRunLaterCallIndex(callIndex.incrementAndGet()));
        model.setPostConstructPostRunLaterCallIndex(callIndex.incrementAndGet());
    }

    @RemotingAction(RUN_LATER_ACTION_NAME)
    public void runLaterAction() {
        model.setActionPreRunLaterCallIndex(callIndex.incrementAndGet());
        sessionExecutor.runLaterInClientSession(() -> model.setActionRunLaterCallIndex(callIndex.incrementAndGet()));
        model.setActionPostRunLaterCallIndex(callIndex.incrementAndGet());
    }

    @RemotingAction(RUN_LATER_ASYNC_ACTION_NAME)
    public void runLaterAsyncAction() {
        model.setActionPreRunLaterAsyncCallIndex(callIndex.incrementAndGet());
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Error!", e);
            }
            sessionExecutor.runLaterInClientSession(() -> model.setActionRunLaterAsyncCallIndex(callIndex.incrementAndGet()));
        });
        model.setActionPostRunLaterAsyncCallIndex(callIndex.incrementAndGet());
    }


    private void resetModel() {
        callIndex.set(0);
        model.setPostConstructPreRunLaterCallIndex(-1);
        model.setPostConstructRunLaterCallIndex(-1);
        model.setPostConstructPostRunLaterCallIndex(-1);
        model.setActionPreRunLaterCallIndex(-1);
        model.setActionRunLaterCallIndex(-1);
        model.setActionPostRunLaterCallIndex(-1);
        model.setActionPreRunLaterAsyncCallIndex(-1);
        model.setActionRunLaterAsyncCallIndex(-1);
        model.setActionPostRunLaterAsyncCallIndex(-1);
    }
}
