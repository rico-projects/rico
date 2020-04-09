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
public class LongRemotingBinder extends AbstractNumericRemotingBinder<Long> {

    public LongRemotingBinder(final Property<Long> property) {
        super(property);
    }

    @Override
    protected boolean equals(final Number n, final Long aLong) {
        if (n == null && aLong != null) {
            return false;
        }
        if (n != null && aLong == null) {
            return false;
        }
        if (n == null && aLong == null) {
            return true;
        }
        return  n.longValue() - aLong.longValue() == 0l;
    }

    @Override
    protected BidirectionalConverter<Number, Long> getConverter() {
        return new BidirectionalConverter<Number, Long>() {
            @Override
            public Number convertBack(final Long value) {
                if (value == null) {
                    return 0l;
                }
                return value;
            }

            @Override
            public Long convert(final Number value) {
                if (value == null) {
                    return 0l;
                }
                return value.longValue();
            }
        };
    }

}

