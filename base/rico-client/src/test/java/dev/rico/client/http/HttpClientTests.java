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
package dev.rico.client.http;

import dev.rico.client.Client;
import dev.rico.internal.core.http.HttpHeaderConstants;
import dev.rico.core.http.BadResponseException;
import dev.rico.core.http.ByteArrayProvider;
import dev.rico.core.http.ConnectionException;
import dev.rico.core.http.HttpClient;
import dev.rico.core.http.HttpResponse;
import com.google.gson.Gson;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.Redirect;
import spark.Spark;

import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.rico.internal.core.http.HttpStatus.SC_HTTP_RESOURCE_NOTFOUND;
import static dev.rico.internal.core.http.HttpStatus.SC_HTTP_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class HttpClientTests {
    private static final String STD_GET_RESPONSE = "Spark Server for HTTP client integration tests";
    private static final String STD_POST_RESPONSE = "CHECK";

    //Maybe we can use http://wiremock.org/docs/getting-started/

    private final int freePort;

    public HttpClientTests() {
        freePort = getFreePort();
    }

    private int getFreePort() {
        final int freePort;
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            freePort = socket.getLocalPort();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return freePort;
    }

    @BeforeClass
    public void startSpark() {
        Spark.port(freePort);
        Spark.get("/", (req, res) -> STD_GET_RESPONSE);
        Spark.get("/error", (req, res) -> {
            res.status(401);
            return "UPPS";
        });
        Spark.post("/", (req, res) -> STD_POST_RESPONSE);
        Spark.get("/", HttpHeaderConstants.JSON_MIME_TYPE, (req, res) -> {

            final Gson gson = new Gson();
            final DummyJson dummy = new DummyJson("Joe", 33, true);
            final String json = gson.toJson(dummy);
            res.type(HttpHeaderConstants.JSON_MIME_TYPE);

            return json;
        });
        Spark.redirect.any("/movedPermanently", "/", Redirect.Status.MOVED_PERMANENTLY);
        Spark.awaitInitialization();
    }

    @AfterClass
    public void destroySpark() {
        Spark.stop();
    }

    @Test
    public void testSimpleGetWithPromise() throws Exception {
        //given:
        final HttpClient client = Client.getService(HttpClient.class);
        final AtomicBoolean actionCalled = new AtomicBoolean(false);
        final AtomicBoolean doneCalled = new AtomicBoolean(false);
        final AtomicBoolean errorCalled = new AtomicBoolean(false);

        //when:
        final CompletableFuture<HttpResponse<Void>> future = getHttpResponseCompletableFuture(client, actionCalled, doneCalled, errorCalled);

        //then:
        final HttpResponse<Void> response = future.get(1_000, TimeUnit.MILLISECONDS);
        assertThat("response not defined", response, notNullValue());
        assertThat("Wrong response code", response.getStatusCode(), is(200));

        assertThatDoneCalledAndErrorNotCalled(actionCalled, doneCalled, errorCalled);
    }


    @Test
    public void testSimpleGetWithStringContent() throws Exception {
        //given:
        final HttpClient client = Client.getService(HttpClient.class);
        final AtomicBoolean actionCalled = new AtomicBoolean(false);
        final AtomicBoolean doneCalled = new AtomicBoolean(false);
        final AtomicBoolean errorCalled = new AtomicBoolean(false);

        //when:
        final CompletableFuture<HttpResponse<String>> future = client.get("http://localhost:" + freePort)
                .withoutContent()
                .readString()
                .onDone(response -> {
                    actionCalled.set(true);
                    doneCalled.set(true);
                })
                .onError(e -> {
                    actionCalled.set(true);
                    errorCalled.set(true);
                })
                .execute();

        //then:
        final HttpResponse<String> response = future.get(1_000, TimeUnit.MILLISECONDS);
        assertThat("response not defined", response, notNullValue());
        assertThat("Wrong response code", response.getStatusCode(), is(200));
        assertThat("Content should not be null", response.getContent(), is(STD_GET_RESPONSE));

        final String content = response.getContent();
        assertThat("String content does not match", content, is(STD_GET_RESPONSE));

        assertThatDoneCalledAndErrorNotCalled(actionCalled, doneCalled, errorCalled);
    }

    @Test
    public void testSimpleGetWithByteContent() throws Exception {
        //given:
        final HttpClient client = Client.getService(HttpClient.class);
        final AtomicBoolean actionCalled = new AtomicBoolean(false);
        final AtomicBoolean doneCalled = new AtomicBoolean(false);
        final AtomicBoolean errorCalled = new AtomicBoolean(false);

        //when:
        final CompletableFuture<HttpResponse<ByteArrayProvider>> future = client.get("http://localhost:" + freePort)
                .withoutContent()
                .readBytes()
                .onDone(response -> {
                    actionCalled.set(true);
                    doneCalled.set(true);
                })
                .onError(e -> {
                    actionCalled.set(true);
                    errorCalled.set(true);
                })
                .execute();

        //then:
        final HttpResponse<ByteArrayProvider> response = future.get(1_000, TimeUnit.MILLISECONDS);
        assertThat("response not defined", response, notNullValue());
        assertThat("Wrong response code", response.getStatusCode(), is(200));

        final byte[] bytes = response.getContent().get();
        assertThat("Byte content does not match", bytes, is(STD_GET_RESPONSE.getBytes()));

        assertThatDoneCalledAndErrorNotCalled(actionCalled, doneCalled, errorCalled);
    }

    @Test
    public void testSimpleGetWithJsonContentType() throws Exception {
        //given:
        final HttpClient client = Client.getService(HttpClient.class);
        final AtomicBoolean actionCalled = new AtomicBoolean(false);
        final AtomicBoolean doneCalled = new AtomicBoolean(false);
        final AtomicBoolean errorCalled = new AtomicBoolean(false);

        //when:
        final CompletableFuture<HttpResponse<String>> future = client.get("http://localhost:" + freePort)
                .withoutContent()
                .readString(HttpHeaderConstants.JSON_MIME_TYPE)
                .onDone(response -> {
                    actionCalled.set(true);
                    doneCalled.set(true);
                })
                .onError(e -> {
                    actionCalled.set(true);
                    errorCalled.set(true);
                })
                .execute();

        //then:
        final HttpResponse<String> response = future.get(1_000, TimeUnit.MILLISECONDS);
        assertThat("response not defined", response, notNullValue());
        assertThat("Wrong response code", response.getStatusCode(), is(200));
        assertThat("Content should not be null", response.getContent(), notNullValue());

        final String json = response.getContent();
        final Gson gson = new Gson();
        final DummyJson dummy = gson.fromJson(json, DummyJson.class);
        assertThat("No JSON object created", dummy, notNullValue());
        assertThat("Wrong name", dummy.getName(), is("Joe"));
        assertThat("Wrong age", dummy.getAge(), is(33));
        assertThat("Wrong isJavaChampion", dummy.isJavaChampion(), is(true));

        assertThatDoneCalledAndErrorNotCalled(actionCalled, doneCalled, errorCalled);
    }

    @Test
    public void testPostWithContent() throws Exception {
        //given:
        final HttpClient client = Client.getService(HttpClient.class);
        final AtomicBoolean actionCalled = new AtomicBoolean(false);
        final AtomicBoolean doneCalled = new AtomicBoolean(false);
        final AtomicBoolean errorCalled = new AtomicBoolean(false);

        //when:
        final CompletableFuture<HttpResponse<String>> future = client.post("http://localhost:" + freePort)
                .withContent("String")
                .readString()
                .onDone(response -> {
                    actionCalled.set(true);
                    doneCalled.set(true);
                })
                .onError(e -> {
                    actionCalled.set(true);
                    errorCalled.set(true);
                })
                .execute();

        //then:
        final HttpResponse<String> response = future.get(10_000, TimeUnit.MILLISECONDS);
        assertThat("response not defined", response, notNullValue());
        assertThat("Wrong response code", response.getStatusCode(), is(200));
        assertThat("Content should not be null", response.getContent(), is(STD_POST_RESPONSE));

        final String content = response.getContent();
        assertThat("String content does not match", content, is(STD_POST_RESPONSE));

        assertThatDoneCalledAndErrorNotCalled(actionCalled, doneCalled, errorCalled);
    }

    @Test
    public void testPostWithoutContent() throws Exception {
        //given:
        final HttpClient client = Client.getService(HttpClient.class);
        final AtomicBoolean actionCalled = new AtomicBoolean(false);
        final AtomicBoolean doneCalled = new AtomicBoolean(false);
        final AtomicBoolean errorCalled = new AtomicBoolean(false);

        //when:
        final CompletableFuture<HttpResponse<String>> future = client.post("http://localhost:" + freePort)
                .withoutContent()
                .readString()
                .onDone(response -> {
                    actionCalled.set(true);
                    doneCalled.set(true);
                })
                .onError(e -> {
                    actionCalled.set(true);
                    errorCalled.set(true);
                })
                .execute();

        //then:
        final HttpResponse<String> response = future.get(1_000, TimeUnit.MILLISECONDS);
        assertThat("response not defined", response, notNullValue());
        assertThat("Wrong response code", response.getStatusCode(), is(200));
        assertThat("Content should not be null", response.getContent(), is(STD_POST_RESPONSE));

        final String content = response.getContent();
        assertThat("String content does not match", content, is(STD_POST_RESPONSE));

        assertThatDoneCalledAndErrorNotCalled(actionCalled, doneCalled, errorCalled);
    }

    @Test
    public void testBadEndpoint() throws Exception {
        //given:
        final HttpClient client = Client.getService(HttpClient.class);
        final AtomicBoolean actionCalled = new AtomicBoolean(false);
        final AtomicBoolean doneCalled = new AtomicBoolean(false);
        final AtomicBoolean errorCalled = new AtomicBoolean(false);

        //when:
        final CompletableFuture<HttpResponse<Void>> future = client.get("http://localhost:" + freePort + "/not/available")
                .withoutContent()
                .withoutResult()
                .onDone(response -> {
                    actionCalled.set(true);
                    doneCalled.set(true);
                })
                .onError(e -> {
                    actionCalled.set(true);
                    errorCalled.set(true);
                })
                .execute();

        //then:
        assertThatBadResponseException(doneCalled, errorCalled, future, SC_HTTP_RESOURCE_NOTFOUND);
        assertThatErrorCalledAndDoneNotCalled(actionCalled, doneCalled, errorCalled);
    }

    @Test
    public void testBadConnection() throws Exception {
        //given:
        final HttpClient client = Client.getService(HttpClient.class);
        final AtomicBoolean actionCalled = new AtomicBoolean(false);
        final AtomicBoolean doneCalled = new AtomicBoolean(false);
        final AtomicBoolean errorCalled = new AtomicBoolean(false);

        //when:
        final CompletableFuture<HttpResponse<Void>> future = client.get("http://localhost:" + getFreePort())
                .withoutContent()
                .withoutResult()
                .onDone(response -> {
                    actionCalled.set(true);
                    doneCalled.set(true);
                })
                .onError(e -> {
                    actionCalled.set(true);
                    errorCalled.set(true);
                })
                .execute();

        //then:
        assertThatConnectionException(future);
        assertThatErrorCalledAndDoneNotCalled(actionCalled, doneCalled, errorCalled);
    }

    @Test
    public void testBadResponse() throws Exception {
        //given:
        final HttpClient client = Client.getService(HttpClient.class);
        final AtomicBoolean actionCalled = new AtomicBoolean(false);
        final AtomicBoolean doneCalled = new AtomicBoolean(false);
        final AtomicBoolean errorCalled = new AtomicBoolean(false);

        //when:
        final CompletableFuture<HttpResponse<Void>> future = client.get("http://localhost:" + freePort + "/error")
                .withoutContent()
                .withoutResult()
                .onDone(response -> {
                    actionCalled.set(true);
                    doneCalled.set(true);
                })
                .onError(e -> {
                    actionCalled.set(true);
                    errorCalled.set(true);
                })
                .execute();

        //then:
        assertThatBadResponseException(doneCalled, errorCalled, future, SC_HTTP_UNAUTHORIZED);
        assertThatErrorCalledAndDoneNotCalled(actionCalled, doneCalled, errorCalled);
    }

    @Test
    public void testPermanentRedirect() throws Exception {
        //given:
        final HttpClient client = Client.getService(HttpClient.class);
        final AtomicBoolean actionCalled = new AtomicBoolean(false);
        final AtomicBoolean doneCalled = new AtomicBoolean(false);
        final AtomicBoolean errorCalled = new AtomicBoolean(false);

        //when:
        final CompletableFuture<HttpResponse<Void>> future = client.get("http://localhost:" + freePort + "/movedPermanently")
                .withoutContent()
                .withoutResult()
                .onDone(response -> {
                    actionCalled.set(true);
                    doneCalled.set(true);
                })
                .onError(e -> {
                    actionCalled.set(true);
                    errorCalled.set(true);
                })
                .execute();

        //then:
        final HttpResponse<Void> response = future.get(1_000, TimeUnit.MILLISECONDS);
        assertThat("response not defined", response, notNullValue());
        assertThat("Wrong response code", response.getStatusCode(), is(200));
    }

    private CompletableFuture<HttpResponse<Void>> getHttpResponseCompletableFuture(final HttpClient client, final AtomicBoolean actionCalled, final AtomicBoolean doneCalled, final AtomicBoolean errorCalled) {
        return client.get("http://localhost:" + freePort)
                .withoutContent()
                .withoutResult()
                .onDone(response -> {
                    actionCalled.set(true);
                    doneCalled.set(true);
                })
                .onError(e -> {
                    actionCalled.set(true);
                    errorCalled.set(true);
                })
                .execute();
    }

    private void retry(final AtomicBoolean actionCalled) throws InterruptedException {
        int retries = 0;
        while(!actionCalled.get() && retries < 5) {
            Thread.sleep(1_000);
            retries++;
        }
    }

    private void assertThatDoneCalledAndErrorNotCalled(final AtomicBoolean actionCalled, final AtomicBoolean doneCalled, final AtomicBoolean errorCalled) throws InterruptedException {
        retry(actionCalled);

        assertThat("Neither Done or Error callable was called", actionCalled.get(), is(true));
        assertThat("Done callable was not called", doneCalled.get(), is(true));
        assertThat("Error callable was called", errorCalled.get(), is(false));
    }

    private void assertThatErrorCalledAndDoneNotCalled(final AtomicBoolean actionCalled, final AtomicBoolean doneCalled, final AtomicBoolean errorCalled) throws InterruptedException {
        retry(actionCalled);

        assertThat("Neither Done or Error callable was called", actionCalled.get(), is(true));
        assertThat("Done callable was called", doneCalled.get(), is(false));
        assertThat("Error callable was not called", errorCalled.get(), is(true));
    }

    private void assertThatBadResponseException(final AtomicBoolean doneCalled, final AtomicBoolean errorCalled, final CompletableFuture<HttpResponse<Void>> future, final int scHttpUnauthorized) throws InterruptedException, java.util.concurrent.TimeoutException {
        try {
            future.get(1_000, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (ExecutionException e) {
            assertThat("Wrong exception type", e.getCause().getClass(), is(BadResponseException.class));
            final BadResponseException badResponseException = (BadResponseException) e.getCause();
            assertThat("Wrong response type", badResponseException.getResponse().getStatusCode(), is(scHttpUnauthorized));
        }
    }

    private void assertThatConnectionException(final CompletableFuture<HttpResponse<Void>> future) throws InterruptedException, java.util.concurrent.TimeoutException {
        try {
            future.get(1_000, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (ExecutionException e) {
            final Throwable internalException = e.getCause();
            assertThat("Wrong exception type", internalException.getClass(), is(ConnectionException.class));
        }
    }

    /**
     * Dummy class for JSON serialization and deserialization.
     */
    public class DummyJson {
        String name;
        int age;
        boolean isJavaChampion;

        public DummyJson(final String name, final int age, final boolean isJavaChampion) {
            this.name = name;
            this.age = age;
            this.isJavaChampion = isJavaChampion;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public boolean isJavaChampion() {
            return isJavaChampion;
        }
    }

}
