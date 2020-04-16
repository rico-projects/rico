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
package dev.rico.internal.remoting.client.javafx;

import dev.rico.remoting.client.javafx.BidirectionalConverter;
import dev.rico.remoting.Property;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class FloatRemotingBinder extends AbstractNumericRemotingBinder<Float> {

    private final static double EPSILON = 1e-10f;

    public FloatRemotingBinder(final Property<Float> property) {
        super(property);
    }

    @Override
    protected boolean equals(final Number n, final Float aFloat) {
        if (n == null && aFloat != null) {
            return false;
        }
        if (n != null && aFloat == null) {
            return false;
        }
        if (n == null && aFloat == null) {
            return true;
        }
        return Math.abs(n.floatValue() - aFloat.floatValue()) < EPSILON;
    }

    @Override
    protected BidirectionalConverter<Number, Float> getConverter() {
        return new BidirectionalConverter<Number, Float>() {
            @Override
            public Number convertBack(final Float value) {
                if (value == null) {
                    return 0.0f;
                }
                return value;
            }

            @Override
            public Float convert(final Number value) {
                if (value == null) {
                    return 0.0f;
                }
                return value.floatValue();
            }
        };
    }

}

