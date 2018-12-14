package dev.rico.mqtt;

public enum Qos {
    Q0, Q1, Q2;

    public static int toInt(Qos qos) {
        return -1;
    }

    public static Qos of(int qos) {
        return null;
    }
}
