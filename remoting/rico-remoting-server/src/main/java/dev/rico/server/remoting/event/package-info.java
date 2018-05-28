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
 * This package provides a server site event bus for the remoting layer. The event bus can be used to send events to other controllers (see {@link dev.rico.server.remoting.RemotingController}) in the same or in external client sessions (see {@link dev.rico.server.client.ClientSession}). Since the eventbus can easily integrated in any managed bean messages can be send from non Rico beans to controllers. By doing so a remoting controller can for example easily react on messages that are send by a REST endpoint.
 *
 * @author Hendrik Ebbers
 */
package dev.rico.server.remoting.event;