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
package dev.rico.internal.remoting.eventbus.distributed;

import dev.rico.internal.remoting.server.distributed.Base64Utils;
import dev.rico.internal.remoting.server.distributed.EventStreamSerializer;
import dev.rico.internal.remoting.server.event.MessageEventImpl;
import dev.rico.remoting.server.event.Topic;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.version.Version;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.Iterator;

import static dev.rico.internal.remoting.server.distributed.DistributedEventConstants.CONTEXT_PARAM;
import static dev.rico.internal.remoting.server.distributed.DistributedEventConstants.DATA_PARAM;
import static dev.rico.internal.remoting.server.distributed.DistributedEventConstants.METADATA_KEY_PARAM;
import static dev.rico.internal.remoting.server.distributed.DistributedEventConstants.METADATA_PARAM;
import static dev.rico.internal.remoting.server.distributed.DistributedEventConstants.METADATA_VALUE_PARAM;
import static dev.rico.internal.remoting.server.distributed.DistributedEventConstants.SPEC_1_0;
import static dev.rico.internal.remoting.server.distributed.DistributedEventConstants.SPEC_VERSION_PARAM;
import static dev.rico.internal.remoting.server.distributed.DistributedEventConstants.TIMESTAMP_PARAM;
import static dev.rico.internal.remoting.server.distributed.DistributedEventConstants.TOPIC_PARAM;

@SuppressWarnings("unchecked")
public class EventStreamSerializerTests {

    @Test
    public void testSimpleEventToJson() throws IOException, ClassNotFoundException {
        //given
        final Topic<String> topic = Topic.create("test-topic");
        final long timestamp = System.currentTimeMillis();
        final String data = "test-data";
        final MessageEventImpl<String> messageEventImpl = new MessageEventImpl<>(topic, timestamp, data);

        //when
        final JsonElement root = convertToJson(messageEventImpl);

        //then
        checkJsonSchema(root);
        Assert.assertEquals(Base64Utils.fromBase64(root.getAsJsonObject().getAsJsonPrimitive(DATA_PARAM).getAsString()), data);
        final JsonObject context = root.getAsJsonObject().get(CONTEXT_PARAM).getAsJsonObject();
        Assert.assertEquals(context.getAsJsonPrimitive(TIMESTAMP_PARAM).getAsNumber().longValue(), timestamp);
        Assert.assertEquals(context.getAsJsonPrimitive(TOPIC_PARAM).getAsString(), topic.getName());
        Assert.assertEquals(context.getAsJsonArray(METADATA_PARAM).size(), 0);
    }

    @Test
    public void testEventWithNullDataToJson() throws IOException, ClassNotFoundException {
        //given
        final Topic<String> topic = Topic.create("test-topic");
        final long timestamp = System.currentTimeMillis();
        final String data = null;
        final MessageEventImpl<String> messageEventImpl = new MessageEventImpl<>(topic, timestamp, data);

        //when
        final JsonElement root = convertToJson(messageEventImpl);

        //then
        checkJsonSchema(root);
        Assert.assertTrue(root.getAsJsonObject().get(DATA_PARAM).isJsonNull());
    }

    @Test
    public void testEventWithSerializedDataToJson() throws IOException, ClassNotFoundException {
        //given
        final Topic<LocalDateTime> topic = Topic.create("test-topic");
        final long timestamp = System.currentTimeMillis();
        final LocalDateTime data = LocalDateTime.now();
        final MessageEventImpl<LocalDateTime> messageEventImpl = new MessageEventImpl<>(topic, timestamp, data);

        //when
        final JsonElement root = convertToJson(messageEventImpl);

        //then
        checkJsonSchema(root);
        Assert.assertEquals(Base64Utils.fromBase64(root.getAsJsonObject().getAsJsonPrimitive(DATA_PARAM).getAsString()), data);
    }

    @Test
    public void testEventWithMetadataToJson() throws IOException, ClassNotFoundException {
        //given
        final Topic<String> topic = Topic.create("test-topic");
        final long timestamp = System.currentTimeMillis();
        final String data = "test-data";
        final MessageEventImpl<String> messageEventImpl = new MessageEventImpl<>(topic, timestamp, data);

        final String key1 = "test-key-1";
        final Serializable value1 = "test-value-1";

        final String key2 = "test-key-2";
        final Serializable value2 = null;

        final String key3 = "test-key-3";
        final Serializable value3 = LocalDateTime.now();

        messageEventImpl.addMetadata(key1, value1);
        messageEventImpl.addMetadata(key2, value2);
        messageEventImpl.addMetadata(key3, value3);

        //when
        final JsonElement root = convertToJson(messageEventImpl);

        //then
        checkJsonSchema(root);
        final JsonArray metadata = root.getAsJsonObject().getAsJsonObject(CONTEXT_PARAM).getAsJsonArray(METADATA_PARAM);
        Assert.assertEquals(metadata.size(), 3);
        Assert.assertEquals(getMetadataValueForKey(metadata, key1), value1);
        Assert.assertEquals(getMetadataValueForKey(metadata, key2), value2);
        Assert.assertEquals(getMetadataValueForKey(metadata, key3), value3);
    }

    @Test
    public void testSimpleEventFromJson() throws IOException, ClassNotFoundException {
        //given
        final Topic<String> topic = Topic.create("test-topic");
        final long timestamp = System.currentTimeMillis();
        final String data = "test-data";

        final JsonObject root = new JsonObject();
        root.addProperty(SPEC_VERSION_PARAM, SPEC_1_0);
        root.addProperty(DATA_PARAM, Base64Utils.toBase64(data));
        final JsonObject context = new JsonObject();
        context.addProperty(TIMESTAMP_PARAM, timestamp);
        context.addProperty(TOPIC_PARAM, topic.getName());
        context.add(METADATA_PARAM, new JsonArray());
        root.add(CONTEXT_PARAM, context);

        checkJsonSchema(root);

        //when
        final MessageEventImpl<?> event = convertToEvent(root);

        //then
        Assert.assertNotNull(event);
        Assert.assertEquals(event.getData(), data);
        Assert.assertEquals(event.getMessageEventContext().getTimestamp(), timestamp);
        Assert.assertEquals(event.getMessageEventContext().getTopic(), topic);
        Assert.assertTrue(event.getMessageEventContext().getMetadata().isEmpty());
    }

    @Test
    public void testEventWithNullDataFromJson() throws IOException, ClassNotFoundException {
        //given
        final Topic<String> topic = Topic.create("test-topic");
        final long timestamp = System.currentTimeMillis();

        final JsonObject root = new JsonObject();
        root.addProperty(SPEC_VERSION_PARAM, SPEC_1_0);
        root.add(DATA_PARAM, JsonNull.INSTANCE);
        final JsonObject context = new JsonObject();
        context.addProperty(TIMESTAMP_PARAM, timestamp);
        context.addProperty(TOPIC_PARAM, topic.getName());
        context.add(METADATA_PARAM, new JsonArray());
        root.add(CONTEXT_PARAM, context);
        checkJsonSchema(root);

        //when
        final MessageEventImpl<?> event = convertToEvent(root);

        //then
        Assert.assertEquals(event.getData(), null);
    }

    private Serializable getMetadataValueForKey(final JsonArray metadataArray, final String key) throws IOException, ClassNotFoundException {
        final Iterator<JsonElement> elementIterator = metadataArray.iterator();
        while (elementIterator.hasNext()) {
            final JsonObject metadata = elementIterator.next().getAsJsonObject();
            if (metadata.getAsJsonPrimitive(METADATA_KEY_PARAM).getAsString().equals(key)) {
                if (metadata.get(METADATA_VALUE_PARAM).isJsonNull()) {
                    return null;
                } else {
                    return Base64Utils.fromBase64(metadata.getAsJsonPrimitive(METADATA_VALUE_PARAM).getAsString());
                }
            }
        }
        Assert.fail("metadata do not contain key '" + key + "'");
        throw new IllegalStateException("metadata do not contain key '" + key + "'");
    }

    private <T extends Serializable> JsonElement convertToJson(MessageEventImpl<T> event) throws IOException {
        final EventStreamSerializer streamSerializer = new EventStreamSerializer();
        final ObjectDataOutput output = new ByteObjectDataOutput();
        streamSerializer.write(output, event);
        final byte[] rawOutputData = output.toByteArray();
        final String outputData = new String(rawOutputData);
        return new JsonParser().parse(outputData);
    }

    private <T extends Serializable> MessageEventImpl<T> convertToEvent(final JsonElement jsonElement) throws IOException {
        final EventStreamSerializer streamSerializer = new EventStreamSerializer();
        final ObjectDataInput input = new JsonBasedObjectDataInput(jsonElement);
        return (MessageEventImpl<T>) streamSerializer.read(input);
    }

    private void checkJsonSchema(final JsonElement root) {
        Assert.assertNotNull(root);
        Assert.assertTrue(root.isJsonObject());

        Assert.assertTrue(root.getAsJsonObject().has(SPEC_VERSION_PARAM));
        Assert.assertTrue(root.getAsJsonObject().get(SPEC_VERSION_PARAM).isJsonPrimitive());
        Assert.assertTrue(root.getAsJsonObject().getAsJsonPrimitive(SPEC_VERSION_PARAM).isString());
        Assert.assertEquals(root.getAsJsonObject().getAsJsonPrimitive(SPEC_VERSION_PARAM).getAsString(), SPEC_1_0);

        Assert.assertTrue(root.getAsJsonObject().has(DATA_PARAM));
        Assert.assertTrue(root.getAsJsonObject().get(DATA_PARAM).isJsonPrimitive() || root.getAsJsonObject().get(DATA_PARAM).isJsonNull());
        if (root.getAsJsonObject().get(DATA_PARAM).isJsonPrimitive()) {
            Assert.assertTrue(root.getAsJsonObject().getAsJsonPrimitive(DATA_PARAM).isString());
        }

        Assert.assertTrue(root.getAsJsonObject().has(CONTEXT_PARAM));
        Assert.assertTrue(root.getAsJsonObject().get(CONTEXT_PARAM).isJsonObject());

        final JsonObject context = root.getAsJsonObject().getAsJsonObject(CONTEXT_PARAM);

        Assert.assertTrue(context.has(TIMESTAMP_PARAM));
        Assert.assertTrue(context.get(TIMESTAMP_PARAM).isJsonPrimitive());
        Assert.assertTrue(context.getAsJsonPrimitive(TIMESTAMP_PARAM).isNumber());

        Assert.assertTrue(context.has(TOPIC_PARAM));
        Assert.assertTrue(context.get(TOPIC_PARAM).isJsonPrimitive());
        Assert.assertTrue(context.getAsJsonPrimitive(TOPIC_PARAM).isString());

        Assert.assertTrue(context.has(METADATA_PARAM));
        Assert.assertTrue(context.get(METADATA_PARAM).isJsonArray());
        final JsonArray metadataArray = context.getAsJsonArray(METADATA_PARAM);
        if (metadataArray.size() > 0) {
            final Iterator<JsonElement> elementIterator = metadataArray.iterator();
            while (elementIterator.hasNext()) {
                final JsonElement metadataElem = elementIterator.next();
                Assert.assertTrue(metadataElem.isJsonObject());
                final JsonObject metadata = metadataElem.getAsJsonObject();
                Assert.assertTrue(metadata.has(METADATA_KEY_PARAM));
                Assert.assertTrue(metadata.get(METADATA_KEY_PARAM).isJsonPrimitive());
                Assert.assertTrue(metadata.getAsJsonPrimitive(METADATA_KEY_PARAM).isString());

                Assert.assertTrue(metadata.has(METADATA_VALUE_PARAM));
                Assert.assertTrue(metadata.get(METADATA_VALUE_PARAM).isJsonPrimitive() ||
                        metadata.get(METADATA_VALUE_PARAM).isJsonNull());
                if (metadata.get(METADATA_VALUE_PARAM).isJsonPrimitive()) {
                    Assert.assertTrue(metadata.getAsJsonPrimitive(METADATA_VALUE_PARAM).isString());
                }
            }
        }
    }

    private class JsonBasedObjectDataInput implements ObjectDataInput {

        private final JsonElement jsonElement;

        public JsonBasedObjectDataInput(final JsonElement jsonElement) {
            this.jsonElement = jsonElement;
        }

        @Override
        public byte[] readByteArray() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public boolean[] readBooleanArray() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public char[] readCharArray() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public int[] readIntArray() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public long[] readLongArray() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public double[] readDoubleArray() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public float[] readFloatArray() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public short[] readShortArray() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public String[] readUTFArray() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public <T> T readObject() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public <T> T readDataAsObject() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public <T> T readObject(final Class aClass) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public Data readData() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public ClassLoader getClassLoader() {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public ByteOrder getByteOrder() {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void readFully(byte[] b) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void readFully(byte[] b, int off, int len) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public int skipBytes(int n) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public boolean readBoolean() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public byte readByte() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public int readUnsignedByte() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public short readShort() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public int readUnsignedShort() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public char readChar() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public int readInt() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public long readLong() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public float readFloat() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public double readDouble() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public String readLine() throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public String readUTF() throws IOException {
            return new GsonBuilder().serializeNulls().create().toJson(jsonElement);
        }

        @Override
        public Version getVersion() {
            return Version.UNKNOWN;
        }
    }

    private class ByteObjectDataOutput implements ObjectDataOutput {

        private final StringBuffer content = new StringBuffer();


        @Override
        public void writeByteArray(final byte[] bytes) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeBooleanArray(final boolean[] booleans) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeCharArray(final char[] chars) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeIntArray(final int[] ints) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeLongArray(final long[] longs) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeDoubleArray(final double[] values) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeFloatArray(final float[] values) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeShortArray(final short[] values) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeUTFArray(final String[] values) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeObject(final Object object) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeData(final Data data) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public byte[] toByteArray() {
            return content.toString().getBytes();
        }

        @Override
        public byte[] toByteArray(final int padding) {
            return new byte[padding];
        }

        @Override
        public ByteOrder getByteOrder() {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void write(final int b) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void write(final byte[] b) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeBoolean(final boolean v) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeByte(final int v) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeShort(final int v) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeChar(final int v) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeInt(final int v) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeLong(final long v) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeFloat(final float v) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeDouble(final double v) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeBytes(final String s) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeChars(final String s) throws IOException {
            throw new RuntimeException("Not needed for test");
        }

        @Override
        public void writeUTF(final String s) throws IOException {
            content.append(s);
        }

        @Override
        public Version getVersion() {
            return Version.UNKNOWN;
        }
    }

}
