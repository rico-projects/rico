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
package dev.rico.internal.remoting.validation;

import dev.rico.internal.remoting.MockedProperty;
import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.Property;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.math.BigInteger;

@RemotingBean
public class TestBeanMin {
    @Min(1)
    private Property<BigDecimal> bigDecimal = new MockedProperty<>();

    @Min(1)
    private Property<BigInteger> bigInteger = new MockedProperty<>();

    @Min(1)
    private Property<Long> longProperty = new MockedProperty<>();

    @Min(1)
    private Property<Byte> byteProperty = new MockedProperty<>();

    @Min(1)
    private Property<String> string = new MockedProperty<>();

    public Property<BigDecimal> bigDecimalProperty() {
        return bigDecimal;
    }

    public Property<BigInteger> bigIntegerProperty() {
        return bigInteger;
    }

    public Property<Long> longProperty() {
        return longProperty;
    }

    public Property<Byte> byteProperty() {
        return byteProperty;
    }

    public Property<String> stringProperty() {
        return string;
    }
}
