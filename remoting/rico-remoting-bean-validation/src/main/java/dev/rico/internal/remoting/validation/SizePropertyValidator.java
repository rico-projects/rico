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
import javax.validation.constraints.Size;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class SizePropertyValidator extends AbstractPropertyValidator<Size, String> {

    private int maxSizeValue;
    private int minSizeValue;

    public SizePropertyValidator() {
        super(String.class);
    }

    @Override
    public void initialize(Size constraintAnnotation) {
        this.minSizeValue = constraintAnnotation.min();
        this.maxSizeValue = constraintAnnotation.max();
    }

    @Override
    protected boolean checkValid(String value, ConstraintValidatorContext context) {
        return (value != null && value.length() >= minSizeValue && value.length() <= maxSizeValue);
    }
}
