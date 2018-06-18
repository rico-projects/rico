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
/**
 * This package contains a first garbage collection implementation for the remoting model.
 * As defined by {@link dev.rico.server.remoting.RemotingModel} and {@link dev.rico.remoting.RemotingBean} each
 * remoting model is a hierarchy of several remoting beans. All the beans will automatically be synchronized
 * between server and client. To do so the remoting layer holds a representation of each remoting bean.
 * When the server controller removes a bean out of the hierarchy the garbage collection should notice this and automatically
 * removePresentationModel the bean representation from the remoting layer. By doing so the bean will automatically be deleted on the client.
 * How a bean can be created and removed is defined by the {@link dev.rico.server.remoting.BeanManager}. by using the garbage collection
 * an application developer doesn't need to call {@link dev.rico.server.remoting.BeanManager#remove(Object)} anymore.
 *
 * The current version of the garbage collections don't allow cycles in references.
 */
package dev.rico.internal.server.remoting.gc;