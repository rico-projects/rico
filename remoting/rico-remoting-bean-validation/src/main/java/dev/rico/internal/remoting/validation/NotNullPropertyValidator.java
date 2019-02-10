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

import dev.rico.remoting.Property;
import org.apiguardian.api.API;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Validator that adds {@link dev.rico.remoting.Property} support for the {@link NotNull} annotation.
 */
@API(since = "0.19.0", status = INTERNAL)
public final class NotNullPropertyValidator implements ConstraintValidator<NotNull, Property> {

    @Override
    public void initialize(final NotNull constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Property value,
                           final ConstraintValidatorContext context) {
        if (value == null || value.get() == null) {
            return false;
        }
        return true;
    }

}

