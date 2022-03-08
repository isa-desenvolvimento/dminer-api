package com.dminer.components;

public class AwaitTime {
    
    public static final Long segundos_3 = 3000L;
    public static final Long segundos_5 = 5000L;
    public static final Long segundos_10 = 10000L;

    public static void waitUntil(long timestamp) {
        long millis = timestamp - System.currentTimeMillis();
        // return immediately if time is already in the past
        // if (millis <= 0)
        //     return;
        try {
            System.out.println("Esperando " + (timestamp / 60) + " segundo...");
            Thread.sleep(timestamp);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}