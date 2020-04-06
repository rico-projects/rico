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
package dev.rico.internal.server.remoting.servlet;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.remoting.context.ServerRemotingContext;
import dev.rico.internal.server.remoting.context.ServerRemotingContextProvider;
import org.apiguardian.api.API;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class InterruptServlet extends HttpServlet {

    private final ServerRemotingContextProvider contextProvider;

    public InterruptServlet(final ServerRemotingContextProvider contextProvider) {
        this.contextProvider = Assert.requireNonNull(contextProvider, "contextProvider");
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse response) throws ServletException, IOException {
        Assert.requireNonNull(response, "response");
        final ServerRemotingContext currentContext = contextProvider.getCurrentContext();
        if(currentContext == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or wrong client session id");
        } else {
            currentContext.interrupt();
        }
    }
}
