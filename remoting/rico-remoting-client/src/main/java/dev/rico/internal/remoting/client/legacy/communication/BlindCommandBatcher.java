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
package dev.rico.internal.remoting.client.legacy.communication;

import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.remoting.legacy.communication.ValueChangedCommand;
import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.apiguardian.api.API.Status.DEPRECATED;

/**
 * A command batcher that puts all commands in one packet that
 * have no onFinished handler attached (blind commands), which is the typical case
 * for value change and create presentation model commands
 * when synchronizing back to the server.
 */
@API(since = "0.x", status = DEPRECATED)
public class BlindCommandBatcher extends CommandBatcher {

    private static final Logger LOG = LoggerFactory.getLogger(BlindCommandBatcher.class);

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final LinkedList<CommandAndHandler> commandsAndHandlers = new LinkedList<>();

    private final Lock commandsAndHandlersLock = new ReentrantLock();

    /**
     * Time allowed to fill the queue before a batch is assembled
     */
    private long deferMillis = 10;

    /**
     * Must be > 0
     */
    private int maxBatchSize = 100;

    /**
     * when attribute x changes its value from 0 to 1 and then from 1 to 2, merge this into one change from 0 to 2
     */
    private boolean mergeValueChanges = false;

    protected final AtomicBoolean inProcess = new AtomicBoolean(false);

    protected final AtomicBoolean deferralNeeded = new AtomicBoolean(false);

    protected boolean shallWeEvenTryToMerge = false;

    @Override
    public boolean isEmpty() {
        return getWaitingBatches().length() < 1;
    }

    @Override
    public void batch(final CommandAndHandler commandWithHandler) {
        LOG.trace("batching {} with {} handler", commandWithHandler.getCommand(), (commandWithHandler.getHandler() != null ? "" : "out"));

        if (canBeDropped(commandWithHandler)) {
            LOG.trace("dropping duplicate GetPresentationModelCommand");
            return;

        }

        commandsAndHandlersLock.lock();
        try {
            commandsAndHandlers.add(commandWithHandler);
        } finally {
            commandsAndHandlersLock.unlock();
        }


        if (commandWithHandler.isBatchable()) {
            deferralNeeded.set(true);
            if (inProcess.get()) {
                return;

            }

            processDeferred();
        } else {
            processBatch();
        }

    }

    protected boolean canBeDropped(final CommandAndHandler commandWithHandler) {
        return false;
    }

    protected void processDeferred() {
        inProcess.set(true);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int count = getMaxBatchSize();// never wait for more than those
                while (deferralNeeded.get() && count > 0) {
                    count--;
                    deferralNeeded.set(false);
                    try {
                        Thread.sleep(getDeferMillis());
                    } catch (InterruptedException e) {
                        throw new RuntimeException("ERROR", e);
                    }
                    // while we sleep, new requests may have arrived that request deferral
                }

                processBatch();
                inProcess.set(false);
            }

        });
    }

    protected void processBatch() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                getCommandsAndHandlersLock().lock();
                try {
                    final CommandAndHandler last = batchBlinds(getCommandsAndHandlers());// always withContent leading blinds first
                    if (last != null) {
                        // we do have a trailing command with handler and batch it separately
                        getWaitingBatches().add(Arrays.asList(last));
                    }

                    if (!getCommandsAndHandlers().isEmpty()) {
                        processBatch();
                        // this is not so much like recursion, more like a trampoline
                    }

                } finally {
                    getCommandsAndHandlersLock().unlock();
                }

            }

        });
    }

    protected CommandAndHandler batchBlinds(final List<CommandAndHandler> queue) {
        if (queue.isEmpty()) {
            return null;
        }

        final List<CommandAndHandler> blindCommands = new LinkedList();
        int counter = maxBatchSize;
        // we have to check again, since new ones may have arrived since last check
        CommandAndHandler val = take(queue);
        shallWeEvenTryToMerge = false;
        while (val != null && val.isBatchable() && counter-- > 0) {// we do have a blind
            addToBlindsOrMerge(blindCommands, val);
            val = counter != 0 ? take(queue) : null;
        }

        LOG.trace("batching {} blinds", blindCommands.size());
        if (!blindCommands.isEmpty()) {
            getWaitingBatches().add(blindCommands);
        }
        return val;// may be null or a cwh that has a handler
    }

    protected void addToBlindsOrMerge(final List<CommandAndHandler> blindCommands, final CommandAndHandler val) {
        if (!wasMerged(blindCommands, val)) {
            blindCommands.add(val);
            if (val.getCommand() instanceof ValueChangedCommand) {
                shallWeEvenTryToMerge = true;
            }

        }

    }

    protected boolean wasMerged(final List<CommandAndHandler> blindCommands, final CommandAndHandler val) {
        if (!mergeValueChanges) {
            return false;
        }

        if (!shallWeEvenTryToMerge) {
            return false;
        }

        if (blindCommands.isEmpty()) {
            return false;
        }

        if (val.getCommand() == null) {
            return false;
        }

        if (!(val.getCommand() instanceof ValueChangedCommand)) {
            return false;
        }

        final ValueChangedCommand valCmd = (ValueChangedCommand) val.getCommand();

        shallWeEvenTryToMerge = true;


        if (blindCommands.get(blindCommands.size() - 1).getCommand() instanceof ValueChangedCommand) {
            final ValueChangedCommand valueChangedCommand = (ValueChangedCommand) blindCommands.get(blindCommands.size() - 1).getCommand();
            if (valCmd.getAttributeId().equals(valueChangedCommand.getAttributeId())) {
                LOG.trace("merging value changed command for attribute {} with new values {}  -> {}", valueChangedCommand.getAttributeId(), valueChangedCommand.getNewValue(), valCmd.getNewValue());
                valueChangedCommand.setNewValue(valCmd.getNewValue());
                return true;
            }
        }
        return false;
    }

    protected CommandAndHandler take(final List<CommandAndHandler> intern) {
        if (intern.isEmpty()) {
            return null;
        }

        return intern.remove(0);
    }

    public List<CommandAndHandler> getCommandsAndHandlers() {
        return commandsAndHandlers;
    }

    public Lock getCommandsAndHandlersLock() {
        return commandsAndHandlersLock;
    }

    public long getDeferMillis() {
        return deferMillis;
    }

    public void setDeferMillis(final long deferMillis) {
        this.deferMillis = deferMillis;
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public void setMaxBatchSize(final int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public void setMergeValueChanges(final boolean mergeValueChanges) {
        this.mergeValueChanges = mergeValueChanges;
    }

}
