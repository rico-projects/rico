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

import dev.rico.internal.remoting.MockedProperty;
import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.Property;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Size;

import java.util.Set;

import static org.testng.Assert.*;

public class SizePropertyValidatorTest {
    private Validator validator;

    @BeforeClass
    public void setup() {
        Configuration<?> validationConf = Validation.byDefaultProvider().configure();
        validator = validationConf.buildValidatorFactory().getValidator();
    }

    @Test
    public void testCheckValid() throws Exception {
        @RemotingBean
        class TestedClass {
            @Size(min = 3, max = 8)
            private Property<Object> value = new MockedProperty<>();
        }

        TestedClass bean = new TestedClass();
        Set<ConstraintViolation<TestedClass>> violations;
        ConstraintViolation<TestedClass> violation;

        bean.value.set("hola");
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        bean.value.set("holayadios");
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value");

        bean.value.set("ho");
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value");
    }

}