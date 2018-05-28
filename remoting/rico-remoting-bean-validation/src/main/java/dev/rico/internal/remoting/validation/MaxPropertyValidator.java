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

import org.apiguardian.api.API;

import javax.validation.ValidationException;
import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Validator that adds {@link dev.rico.remoting.Property} support for the {@link Max} annotation.
 */
@API(since = "0.19.0", status = INTERNAL)
public class MaxPropertyValidator extends AbstractNumberValidator<Max> {

    private long maxValue;

    @Override
    public void initialize(final Max constraint) {
        super.initialize(constraint);
        maxValue = constraint.value();
    }

    @Override
    protected boolean checkValidLong(final Long value) {
        return value <= maxValue;
    }

    @Override
    protected boolean checkValidCharSequence(final CharSequence value) {
        throw new ValidationException("Max constraint does not support CharSequence validation: " + value);
    }

    @Override
    protected boolean checkValidBigInteger(final BigInteger value) {
        return value.compareTo(BigInteger.valueOf(maxValue) ) != 1;
    }

    @Override
    protected boolean checkValidBigDecimal(final BigDecimal value) {
        return value.compareTo(BigDecimal.valueOf(maxValue) ) != 1;
    }
}
