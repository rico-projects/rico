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
package dev.rico.internal.remoting;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class BeanDefinitionException extends RuntimeException {

    private static final long serialVersionUID = -723698312668668310L;

    public BeanDefinitionException() {
    }

    public BeanDefinitionException(final Class<?> notValidBeanClass) {
        this("Class " + notValidBeanClass + " is not a valid remoting bean class!");
    }

    public BeanDefinitionException(final String message) {
        super(message);
    }

    public BeanDefinitionException(final Class<?> notValidBeanClass, final Throwable cause) {
        this("Class " + notValidBeanClass + " is not a valid remoting bean class!", cause);
    }


    public BeanDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BeanDefinitionException(final Throwable cause) {
        super(cause);
    }
}
