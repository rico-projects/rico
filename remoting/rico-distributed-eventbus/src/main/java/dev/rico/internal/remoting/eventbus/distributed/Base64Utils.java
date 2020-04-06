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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class Base64Utils {

    public static String toBase64(final Serializable data) throws IOException {
        final ByteArrayOutputStream rawOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream dataOutputStream = new ObjectOutputStream(rawOutputStream);
        dataOutputStream.writeObject(data);
        final Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(rawOutputStream.toByteArray());
    }

    public static Serializable fromBase64(final String data) throws IOException, ClassNotFoundException {
        final Base64.Decoder decoder = Base64.getDecoder();
        final byte[] raw = decoder.decode(data);
        final ByteArrayInputStream rawInputStream = new ByteArrayInputStream(raw);
        final ObjectInputStream dataInputStream = new ObjectInputStream(rawInputStream);
        return (Serializable) dataInputStream.readObject();
    }

}
