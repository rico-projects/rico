package dev.rico.internal.core;

public class OsUtil {

    public static boolean isWindows() {
        String operSys = System.getProperty(RicoConstants.OS_NAME).toLowerCase();
        return (operSys.contains(RicoConstants.WIN));
    }

}
