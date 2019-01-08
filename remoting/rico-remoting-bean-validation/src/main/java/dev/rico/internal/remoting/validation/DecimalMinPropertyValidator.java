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
package dev.rico.internal.remoting.validation;

import org.apiguardian.api.API;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Validator that adds {@link dev.rico.remoting.Property} support for the {@link DecimalMin} annotation.
 */
@API(since = "0.19.0", status = INTERNAL)
public final class DecimalMinPropertyValidator extends AbstractNumberValidator<DecimalMin> {

    private BigDecimal minValue;

    private boolean inclusive;

    @Override
    public void initialize(DecimalMin minValue) {
        this.minValue = new BigDecimal(minValue.value() );
        this.inclusive = minValue.inclusive();
    }

    @Override
    protected boolean checkValidLong(Long value) {
        return checkValidBigDecimal(BigDecimal.valueOf(value) );
    }

    @Override
    protected boolean checkValidCharSequence(CharSequence value) {
        return checkValidBigDecimal(new BigDecimal(value.toString()) );
    }

    @Override
    protected boolean checkValidBigInteger(BigInteger value) {
        return checkValidBigDecimal(new BigDecimal(value) );
    }

    @Override
    protected boolean checkValidBigDecimal(BigDecimal value) {
        return inclusive ? value.compareTo(minValue) != -1 : value.compareTo(minValue) == 1;
    }
}


