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
package dev.rico.client.remoting.impl;

import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.client.concurrent.UiExecutor;
import dev.rico.internal.client.remoting.HttpClientConnector;
import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.DefaultModelSynchronizer;
import dev.rico.internal.client.remoting.legacy.communication.SimpleExceptionHandler;
import dev.rico.client.Client;
import dev.rico.internal.client.http.HttpClientImpl;
import dev.rico.internal.core.http.HttpStatus;
import dev.rico.internal.core.RicoConstants;
import dev.rico.internal.remoting.codec.OptimizedJsonCodec;
import dev.rico.internal.remoting.commands.CreateContextCommand;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.remoting.legacy.communication.CreatePresentationModelCommand;
import dev.rico.internal.client.HeadlessToolkit;
import dev.rico.core.http.HttpClient;
import dev.rico.core.http.HttpURLConnectionFactory;
import dev.rico.remoting.RemotingException;
import com.google.gson.Gson;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestHttpClientConnector {

    @Test(enabled = false)
    public void testSimpleCall() throws RemotingException, URISyntaxException {
        Client.init(new HeadlessToolkit());
        Client.getClientConfiguration().setHttpURLConnectionFactory(new HttpURLConnectionFactory() {
            @Override
            public HttpURLConnection create(URI url) throws IOException {
                return new HttpURLConnection(url.toURL()) {
                    @Override
                    public void disconnect() {

                    }

                    @Override
                    public boolean usingProxy() {
                        return false;
                    }

                    @Override
                    public void connect() throws IOException {

                    }

                    @Override
                    public int getResponseCode() throws IOException {
                        return HttpStatus.HTTP_OK;
                    }

                    @Override
                    public OutputStream getOutputStream() throws IOException {
                        return new ByteArrayOutputStream();
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        String response = "[{\"pmId\":\"p1\",\"clientSideOnly\":false,\"id\":\"CreatePresentationModel\",\"attributes\":[],\"pmType\":null,\"className\":\"dev.rico.internal.remoting.legacy.communication.CreatePresentationModelCommand\"}]";
                        return new ByteArrayInputStream(response.getBytes("UTF-8"));
                    }

                    @Override
                    public String getHeaderField(String name) {
                        if (RicoConstants.CLIENT_ID_HTTP_HEADER_NAME.equals(name)) {
                            return "TEST-ID";
                        }
                        return super.getHeaderField(name);
                    }
                };
            }
        });

        final UiExecutor uiExecutor = Client.getService(UiExecutor.class);
        final BackgroundExecutor backgroundExecutor = Client.getService(BackgroundExecutor.class);

        final ClientModelStore clientModelStore = new ClientModelStore(new DefaultModelSynchronizer(() -> null));
        final HttpClientConnector connector = new HttpClientConnector(getDummyURL(), uiExecutor, backgroundExecutor, clientModelStore, OptimizedJsonCodec.getInstance(), new SimpleExceptionHandler(), Client.getService(HttpClient.class));

        final CreatePresentationModelCommand command = new CreatePresentationModelCommand();
        command.setPmId("p1");
        final List<Command> result = connector.transmit(Collections.singletonList(command));

        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.get(0) instanceof CreatePresentationModelCommand);
        Assert.assertEquals(((CreatePresentationModelCommand) result.get(0)).getPmId(), "p1");
    }

    @Test(expectedExceptions = RemotingException.class)
    public void testBadResponse() throws RemotingException, URISyntaxException {
        Client.init(new HeadlessToolkit());
        Client.getClientConfiguration().setHttpURLConnectionFactory(new HttpURLConnectionFactory() {
            @Override
            public HttpURLConnection create(URI url) throws IOException {
                return new HttpURLConnection(url.toURL()) {
                    @Override
                    public void disconnect() {

                    }

                    @Override
                    public boolean usingProxy() {
                        return false;
                    }

                    @Override
                    public void connect() throws IOException {

                    }

                    @Override
                    public OutputStream getOutputStream() throws IOException {
                        return new ByteArrayOutputStream();
                    }

                };
            }
        });

        final ClientModelStore clientModelStore = new ClientModelStore(new DefaultModelSynchronizer(() -> null));

        final UiExecutor uiExecutor = Client.getService(UiExecutor.class);
        final BackgroundExecutor backgroundExecutor = Client.getService(BackgroundExecutor.class);


        final HttpClientConnector connector = new HttpClientConnector(getDummyURL(), uiExecutor, backgroundExecutor, clientModelStore, OptimizedJsonCodec.getInstance(), new SimpleExceptionHandler(), new HttpClientImpl(new Gson(), Client.getClientConfiguration()));

        final List<Command> commands = new ArrayList<>();
        commands.add(new CreateContextCommand());
        connector.transmit(commands);
    }

    private URI getDummyURL(){
        try {
            return new URI("http://dummyURL");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Exception occurred while creating URL", e);
        }
    }
}
