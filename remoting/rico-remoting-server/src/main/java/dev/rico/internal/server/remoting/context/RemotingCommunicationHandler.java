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
package dev.rico.internal.server.remoting.context;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.codec.Codec;
import dev.rico.internal.remoting.communication.commands.Command;
import dev.rico.internal.remoting.communication.commands.impl.CreateContextCommand;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.server.client.ClientSession;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class RemotingCommunicationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RemotingCommunicationHandler.class);

    private final static String REMOTING_CONTEXT_ATTRIBUTE_NAME = "ServerRemotingContext";

    private final ClientSessionProvider sessionProvider;

    private final Codec codec = Codec.getInstance();

    private final RemotingContextFactory contextFactory;

    private static final HashMap<String, WeakReference<ServerRemotingContext>> weakContextMap = new HashMap<>();

    public RemotingCommunicationHandler(final ClientSessionProvider sessionProvider, RemotingContextFactory contextFactory) {
        this.sessionProvider = Assert.requireNonNull(sessionProvider, "sessionProvider");
        this.contextFactory = contextFactory;
    }

    public void handle(final HttpServletRequest request, final HttpServletResponse response) {
        Assert.requireNonNull(request, "request");
        Assert.requireNonNull(response, "response");

        final HttpSession httpSession = Assert.requireNonNull(request.getSession(), "request.getSession()");
        final ClientSession clientSession = sessionProvider.getCurrentClientSession();
        if (clientSession == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            LOG.error("No client session provided for request in http session {}", httpSession.getId());
            return;
        }


        final String userAgent = request.getHeader("user-agent");
        LOG.trace("receiving RPM request for client session {} in http session {} from client with user-agent {}", clientSession.getId(), httpSession.getId(), userAgent);

        final List<Command> commands = new ArrayList<>();
        try {
            commands.addAll(readCommands(request));
        } catch (final Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            LOG.error("Can not parse request! (ServerRemotingContext " + clientSession.getId() + ")", e);
            return;
        }
        LOG.trace("Request for ServerRemotingContext {} in http session {} contains {} commands", clientSession.getId(), httpSession.getId(), commands.size());

        try {
            ServerRemotingContext context = getOrCreateContext(clientSession, commands);

            if(context == null && commands.isEmpty()) {
                return;
            } else if(context == null) {
                throw new IllegalStateException("No context found!");
            }

            final List<Command> results = new ArrayList<>();
            try {
                results.addAll(handle(context, commands));
            } catch (final Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                LOG.error("Can not withoutResult the the received commands (ServerRemotingContext " + context.getId() + ")", e);
                return;
            }




            LOG.trace("Sending RPM response for client session {} in http session {} from client with user-agent {}", context.getId(), httpSession.getId(), userAgent);
            LOG.trace("RPM response for client session {} in http session {} contains {} commands", context.getId(), httpSession.getId(), results.size());

            try {
                writeCommands(results, response);
            } catch (final Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                LOG.error("Can not writeRequestContent response!", e);
                return;
            }
        } catch (final Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            LOG.error("Can not find or createList matching remoting context in session " + httpSession.getId(), e);
            return;
        }
    }

    public ServerRemotingContext getContext(final ClientSession clientSession) {
        Assert.requireNonNull(clientSession, "clientSession");
        return clientSession.getAttribute(REMOTING_CONTEXT_ATTRIBUTE_NAME);
    }

    public ServerRemotingContext getContextById(String clientSessionId) {
        Assert.requireNonBlank(clientSessionId, "clientSessionId");

        WeakReference<ServerRemotingContext> ref = weakContextMap.get(clientSessionId);
        ServerRemotingContext serverRemotingContext = ref.get();
        Assert.requireNonNull(serverRemotingContext, "serverRemotingContext");

        return serverRemotingContext;
    }

    public ServerRemotingContext getCurrentRemotingContext() {
        final ClientSession clientSession = sessionProvider.getCurrentClientSession();
        if (clientSession == null) {
            return null;
        }
        return getContext(clientSession);
    }

    private ServerRemotingContext getOrCreateContext(final ClientSession clientSession, final List<Command> commands) {
        final ServerRemotingContext context = getContext(clientSession);
        if (context != null) {
            return context;
        }
        if (containsInitCommand(commands)) {
            final Consumer<ServerRemotingContext> onDestroyCallback = (remotingContext) -> {
                Assert.requireNonNull(remotingContext, "remotingContext");
                LOG.trace("Destroying ServerRemotingContext {}", remotingContext.getId());
                remove(clientSession);
            };
            ServerRemotingContext createdContext = contextFactory.create(clientSession, onDestroyCallback);
            add(clientSession, createdContext);
            return createdContext;
        }
        return null;
    }

    private boolean containsInitCommand(final List<Command> commands) {
        for (Command command : commands) {
            if (command instanceof CreateContextCommand) {
                return true;
            }
        }
        return false;
    }

    private List<Command> readCommands(final HttpServletRequest request) throws Exception {
        final List<Command> commands = new ArrayList<>();
        final StringBuilder requestJson = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            requestJson.append(line).append("\n");
        }
        commands.addAll(codec.decode(requestJson.toString()));
        return commands;
    }

    private void writeCommands(final List<Command> commands, final HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "application/json");
        response.setCharacterEncoding("UTF-8");
        final String jsonResponse = codec.encode(commands);
        response.getWriter().print(jsonResponse);
    }

    private List<Command> handle(final ServerRemotingContext context, List<Command> commands) {
        final List<Command> results = new ArrayList<>();
        results.addAll(context.handle(commands));
        return results;
    }

    private void add(final ClientSession clientSession, final ServerRemotingContext context) {
        Assert.requireNonNull(clientSession, "clientSession");
        Assert.requireNonNull(context, "context");
        clientSession.setAttribute(REMOTING_CONTEXT_ATTRIBUTE_NAME, context);
        weakContextMap.put(clientSession.getId(), new WeakReference<>(context));
    }

    private void remove(final ClientSession clientSession) {
        Assert.requireNonNull(clientSession, "clientSession");
        clientSession.removeAttribute(REMOTING_CONTEXT_ATTRIBUTE_NAME);
    }

}
