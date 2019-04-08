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
package dev.rico.internal.core.ansi;

import dev.rico.internal.core.PlatformVersion;

import java.util.Optional;

/**
 * Created by hendrikebbers on 23.01.18.
 */
public class PlatformLogo {

    public static void printLogo() {
        final String version = PlatformVersion.getVersion();
        final String versionString = Optional.of(version).map(v -> "Version " + version + " | ").orElse("");
        final String versionEndSuffix = "                       ".substring(Math.min(20,versionString.length()));

        final String strokeColor = getIfAnsiSupported(AnsiOut.ANSI_BLUE);
        final String textColor = getIfAnsiSupported(AnsiOut.ANSI_RED);
        final String borderColor = getIfAnsiSupported(AnsiOut.ANSI_GREEN);


        final String borderStart = getIfAnsiSupported(AnsiOut.ANSI_BOLD) + borderColor;
        final String borderEnd = getIfAnsiSupported(AnsiOut.ANSI_RESET);
        final String borderPipe = borderStart + "|" + borderEnd;
        final String logoStart = getIfAnsiSupported(AnsiOut.ANSI_BOLD) + strokeColor;
        final String textStart = textColor;
        final String textEnd = getIfAnsiSupported(AnsiOut.ANSI_RESET);
        final String boldTextStart = getIfAnsiSupported(AnsiOut.ANSI_BOLD) + textColor;
        final String boldTextEnd = getIfAnsiSupported(AnsiOut.ANSI_RESET);

        System.out.println("");
        System.out.println("  " + borderStart + "____________________________________________________________________________________" + borderEnd);
        System.out.println("  " + borderPipe + logoStart + "   _____  _                  __                                           _       " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "  |  __ \\(_)                / _| _ _  __ _  _ __   ___ __ __ __ ___  _ _ | |__    " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "  | |__) |_  ___ ___       |  _|| '_|/ _` || '  \\ / -_)\\ V  V // _ \\| '_|| / /    " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "  |  _  /| |/ __/ _ \\      | |  |_|  \\__,_||_|_|_|\\___| \\_/\\_/ \\___/|_|  | \\ \\    " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "  | | \\ \\| | (_| (_) |     | |                                           | |\\ \\   " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "  |_|  \\_\\_|\\___\\___/      |_|  " + textStart + versionString + "by " + textEnd + boldTextStart + "dev.karakun.com" + boldTextEnd + versionEndSuffix + logoStart + "|_| \\_\\  " + borderPipe);
        System.out.println("  " + borderPipe + borderStart + "__________________________________________________________________________________" + borderEnd + borderPipe);
        System.out.println("");
        System.out.println("");
    }

    private static String getIfAnsiSupported(final String ansiCode) {
        if(AnsiOut.isSupported()) {
            return ansiCode;
        } else {
            return "";
        }
    }

    public static void main(String[] args) {
        printLogo();
    }
}
