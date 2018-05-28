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
package dev.rico.internal.client;

import static dev.rico.internal.core.RicoConstants.RICO;

public interface ClientConstants {

    String UI_CONTEXT = "uiToolkit";
    String BACKGROUND_EXECUTOR = RICO + ".background.executor";
    String UNCAUGHT_EXCEPTION_HANDLER = RICO + ".background.uncaughtExceptionHandler";
    String UI_EXECUTOR = RICO + ".ui.executor";
    String UI_UNCAUGHT_EXCEPTION_HANDLER = RICO + ".ui.uncaughtExceptionHandler";
    String COOKIE_STORE = RICO + ".http.cookieStore";
    String CONNECTION_FACTORY = RICO + ".http.connectionFactory";
    String CONFIG_DEFAULT_LOCATION = "application.properties";
    String HEADLESS_TOOLKIT = "headless toolkit";
}
