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
package dev.rico.internal.core.http;

public interface HttpHeaderConstants {

    String CONTENT_DISPOSITION_HEADER_NAME = "Content-Disposition";

    String CHARSET = "UTF-8";

    String CONTENT_TYPE_HEADER = "Content-Type";

    String CHARSET_HEADER = "charset";

    String CONTENT_LENGHT_HEADER = "Content-Length";

    String ACCEPT_HEADER = "Accept";

    String COOKIE_HEADER = "Cookie";

    String SET_COOKIE_HEADER = "Set-Cookie";

    String ACCEPT_CHARSET_HEADER = "Accept-Charset";

    String SERVER_TIMING_HEADER = "Server-Timing";

    String SERVER_TIMING_HEADER_DUR = "dur=";

    String SERVER_TIMING_HEADER_DESC = "desc=";

    String JSON_MIME_TYPE = "application/json;charset=utf-8";

    String TEXT_MIME_TYPE = "application/txt;charset=utf-8";

    String RAW_MIME_TYPE = "application/raw";

    String FORM_MIME_TYPE = "application/x-www-form-urlencoded";

}
