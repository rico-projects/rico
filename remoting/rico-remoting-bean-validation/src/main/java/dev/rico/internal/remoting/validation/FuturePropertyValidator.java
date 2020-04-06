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

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Future;
import java.util.Calendar;
import java.util.Date;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Validator that adds {@link dev.rico.remoting.Property} support for the {@link Future} annotation.
 */
@API(since = "0.19.0", status = INTERNAL)
public final class FuturePropertyValidator extends AbstractDateAndCalendarValidator<Future> {

    @Override
    protected boolean checkValidDate(final Date date, final ConstraintValidatorContext context) {
        return date.after(new Date() );
    }

    @Override
    protected boolean checkValidCalendar(final Calendar calendar, final ConstraintValidatorContext context) {
        return calendar.after(Calendar.getInstance() );
    }
}
