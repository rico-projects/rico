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

import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Utility abstract class to minimize {@link Number} type checking. Provides template methods for its implementations.
 * Override this abstract class when implementing number validators.
 * <p>
 *
 * @param <A> defines the annotation of the constraints
 */
@API(since = "0.19.0", status = INTERNAL)
public abstract class AbstractNumberValidator<A extends Annotation> extends AbstractPropertyValidator<A, Number> {

    public AbstractNumberValidator() {
        super(Number.class);
    }

    @Override
    protected boolean checkValid(final Number value, final ConstraintValidatorContext context) {
        if (value instanceof BigDecimal) {
            return checkValidBigDecimal((BigDecimal) value);
        } else if (value instanceof BigInteger) {
            return checkValidBigInteger((BigInteger) value);
        } else if (value instanceof CharSequence) {
            return checkValidCharSequence((CharSequence) value);
        } else if (value instanceof Byte ||
                value instanceof Short ||
                value instanceof Integer ||
                value instanceof Long) {
            return checkValidLong(value.longValue() );
        } else {
            throw new ValidationException("Property contains value of type " +
                    value.getClass() +
                    " whereas only BigDecimal, BigInteger, CharSequence, long, integer, " +
                    "short, byte and their wrappers are supported."
            );
        }
    }

    /**
     * Template method hook for number validations.
     * <p>
     * NOTE: all byte, short, integer, long and their respective wrappers are handled by long for convenience,
     * they are all embraced by long.
     *
     * @param value - the value, guaranteed not-null
     * @return true if the value passes validation, false otherwise
     */
    protected abstract boolean checkValidLong(Long value);

    /**
     * Template method hook for number validations.
     *
     * @param value - the value, guaranteed not-null
     * @return true if the value passes validation, false otherwise
     */
    protected abstract boolean checkValidCharSequence(CharSequence value);

    /**
     * Template method hook for number validations.
     * NOTE: casting types with {@code Big} prefix to {@code long} is not desired, because
     * it will retain only low order digits.
     *
     * @param value - the value, guaranteed not-null
     * @return true if the value passes validation, false otherwise
     */
    protected abstract boolean checkValidBigInteger(BigInteger value);

    /**
     * Template method hook for number validations.
     * NOTE: casting types with {@code Big} prefix to {@code long} is not desired, because
     * it will retain only low order digits.
     *
     * @param value - the value, guaranteed not-null
     * @return true if the value passes validation, false otherwise
     */
    protected abstract boolean checkValidBigDecimal(BigDecimal value);
}
