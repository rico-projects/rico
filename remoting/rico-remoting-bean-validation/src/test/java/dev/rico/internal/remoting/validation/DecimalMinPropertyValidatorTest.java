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

import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.Property;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.*;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/*
 * TODO: this test is base bones, needs more.
 */
public class DecimalMinPropertyValidatorTest {

    @RemotingBean
    private class MinBean {
        // inclusive = true by default
        @DecimalMin(value = "1234.5E-4")
        private Property<BigDecimal> bigDecimal = new MockedProperty<>();

        public Property<BigDecimal> bigDecimalProperty() {
            return bigDecimal;
        }
    }

    private Validator validator;

    @BeforeClass
    public void setup() {
        Configuration<?> validationConf = Validation.byDefaultProvider().configure();
        validator = validationConf.buildValidatorFactory().getValidator();
    }

    @Test
    public void testCheckValidBasic() {
        MinBean bean = new MinBean();

        Set<ConstraintViolation<MinBean>> violations;

        bean.bigDecimalProperty().set(new BigDecimal("1234.6E-4"));
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        bean.bigDecimalProperty().set(new BigDecimal("1234.5E-4"));
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        bean.bigDecimalProperty().set(new BigDecimal("1234.4E-4"));
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
    }

}
