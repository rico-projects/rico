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
package dev.rico.internal.server.validation;

/**
 * @author Gunnar Morling
 * @author Hendrik Ebbers
 */
public interface RemoteConstants {

    String INSTANCE_PROPERTY_NAME = "instance";

    String TYPE_PROPERTY_NAME = "type";

    String IDENTIFIER_PROPERTY_NAME = "identifier";

    String MESSAGE_PROPERTY_NAME = "message";

    String TEMPLATE_PROPERTY_NAME = "template";

    int BAD_REQUEST = 400;

    String CONTENT_TYPE_HEADER_NAME = "Content-Type";

    String JSON_CONTENT_TYPE = "application/json";

    String UTF_8 = "UTF-8";
}
