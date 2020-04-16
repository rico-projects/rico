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
package dev.rico.internal.remoting.converters;

import dev.rico.internal.remoting.RemotingUtils;
import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ConverterFactory;
import dev.rico.remoting.converter.BeanRepo;
import dev.rico.remoting.converter.ValueConverterException;
import org.apiguardian.api.API;

import java.util.Collections;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class BeanConverterFactory implements ConverterFactory {

    public final static int FIELD_TYPE_REMOTING_BEAN = 0;

    private Converter<Object, String> converter;

    @Override
    @SuppressWarnings("deprecation")
    public void init(final BeanRepo beanRepository) {
        this.converter = new RemotingBeanConverter(beanRepository);
    }

    @Override
    public boolean supportsType(final Class<?> cls) {
        return RemotingUtils.isRemotingBean(cls);
    }

    @Override
    public List<Class> getSupportedTypes() {
        return Collections.singletonList(RemotingBean.class);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_REMOTING_BEAN;
    }

    @Override
    public Converter getConverterForType(final Class<?> cls) {
        return converter;
    }

    @SuppressWarnings("deprecation")
    private class RemotingBeanConverter extends AbstractStringConverter<Object> {

        private final BeanRepo beanRepository;

        public RemotingBeanConverter(final BeanRepo beanRepository) {
            this.beanRepository = beanRepository;
        }

        @Override
        public Object convertFromRemoting(final String value) throws ValueConverterException {
            try {
                return beanRepository.getBean(value);
            } catch (Exception e) {
                throw new ValueConverterException("Can not convert bean with id: "+ value +" to remoting bean", e);
            }
        }

        @Override
        public String convertToRemoting(final Object value) throws ValueConverterException {
            try {
                return beanRepository.getRemotingId(value);
            } catch (Exception e) {
                throw new ValueConverterException("Can not convert from remoting bean", e);
            }
        }
    }
}
