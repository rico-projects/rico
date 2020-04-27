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
package dev.rico.internal.logging;

import org.slf4j.spi.MDCAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThreadLocalMDCAdapter implements MDCAdapter {

    private final ThreadLocal<Map<String, String>> mapThreadLocal = new ThreadLocal<>();

    @Override
    public void put(String key, String val) {
        getMap().put(key, val);
    }

    @Override
    public String get(String key) {
        return getMap().get(key);
    }

    @Override
    public void remove(String key) {
        getMap().remove(key);
    }

    @Override
    public void clear() {
        getMap().clear();
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        return Collections.unmodifiableMap(getMap());
    }

    @Override
    public void setContextMap(Map<String, String> contextMap) {
        mapThreadLocal.set(contextMap);
    }

    private Map<String, String> getMap() {
        Map<String, String> map = mapThreadLocal.get();
        if(map == null) {
            map = new HashMap<>();
            mapThreadLocal.set(map);
        }
        return map;
    }
}
