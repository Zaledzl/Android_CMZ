package com.example.dawn.appdesign.util;

public class BeatLock {
    private static boolean lock = false;

    public static boolean isLock() {
        return lock;
    }

    public static void setLock(boolean lock) {
        BeatLock.lock = lock;
    }
}
