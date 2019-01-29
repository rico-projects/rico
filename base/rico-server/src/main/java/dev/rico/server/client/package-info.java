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
/**
 * This package provides the public API that can be used to get access to a client session. A client session is defined as a sub-session of an http session. Based on this a web client can easily be opened in several tabs in a browser. Each of this tabs will handle its own client session while all tabs share a http session.
 *
 * @author Hendrik Ebbers
 *
 * @see dev.rico.server.client.ClientSession
 */
package dev.rico.server.client;