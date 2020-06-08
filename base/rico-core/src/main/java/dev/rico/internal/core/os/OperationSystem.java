/*
 * Copyright 2019 Karakun AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain net.adoptopenjdk.icedteaweb.client.controlpanel.panels.provider.ControlPanelProvider copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.internal.core.os;

public enum OperationSystem {

    LINUX("Linux", "linux"),
    MAC("Mac OS", "mac"),
    WIN("Windows", "win"),
    UNKNOWN("Unknown", "unknown");

    private final String name;

    private final String shortName;

    OperationSystem(final String name, final String shortName) {
        this.name = name;
        this.shortName = shortName;
    }

    public boolean isMac() {
        return this == MAC;
    }

    public boolean isWindows() {
        return this == WIN;
    }

    public boolean isLinux() {
        return this == LINUX;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }
    
    public static OperationSystem getLocalSystem() {
        final String osName = System.getProperty(OperationSystemConstants.OS_SYSTEM_PROPERTY).toLowerCase();
        if (osName.contains(OperationSystemConstants.WIN)) {
            return WIN;
        }
        if (osName.contains(OperationSystemConstants.MAC)) {
            return MAC;
        }
        if (osName.contains(OperationSystemConstants.LINUX)) {
            return LINUX;
        }
        return UNKNOWN;
    }
}
