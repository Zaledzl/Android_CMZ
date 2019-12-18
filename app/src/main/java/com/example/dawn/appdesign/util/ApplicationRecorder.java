package com.example.dawn.appdesign.util;

import android.app.Application;

public class ApplicationRecorder extends Application {

    private static String p1_head;
    private static String p1_body;
    private static String p2_head;
    private static String p2_body;

    private static String p1_8_head;
    private static String p1_8_body;
    private static String p2_8_head;
    private static String p2_8_body;

    private static boolean set;
    private static int diff_score = 10;
    private static int time_all = 60;
    private static String p1_name;
    private static String p2_name;
    private static String p1_weight;
    private static String p2_weight;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void setP1_head(String p1_head) {
        ApplicationRecorder.p1_head = p1_head;
    }

    public static void setP1_body(String p1_body) {
        ApplicationRecorder.p1_body = p1_body;
    }

    public static void setP2_head(String p2_head) {
        ApplicationRecorder.p2_head = p2_head;
    }

    public static void setP2_body(String p2_body) {
        ApplicationRecorder.p2_body = p2_body;
    }

    public static String getP1_head() {
        return p1_head;
    }

    public static String getP1_body() {
        return p1_body;
    }

    public static String getP2_head() {
        return p2_head;
    }

    public static String getP2_body() {
        return p2_body;
    }

    public static String getP1_8_head() {
        return p1_8_head;
    }

    public static void setP1_8_head(String p1_8_head) {
        ApplicationRecorder.p1_8_head = p1_8_head;
    }

    public static String getP1_8_body() {
        return p1_8_body;
    }

    public static void setP1_8_body(String p1_8_body) {
        ApplicationRecorder.p1_8_body = p1_8_body;
    }

    public static String getP2_8_head() {
        return p2_8_head;
    }

    public static void setP2_8_head(String p2_8_head) {
        ApplicationRecorder.p2_8_head = p2_8_head;
    }

    public static String getP2_8_body() {
        return p2_8_body;
    }

    public static void setP2_8_body(String p2_8_body) {
        ApplicationRecorder.p2_8_body = p2_8_body;
    }

    public static boolean isSet() {
        return set;
    }

    public static void setSet(boolean set) {
        ApplicationRecorder.set = set;
    }

    public static String getP1_name() {
        return p1_name;
    }

    public static void setP1_name(String p1_name) {
        ApplicationRecorder.p1_name = p1_name;
    }

    public static String getP2_name() {
        return p2_name;
    }

    public static void setP2_name(String o2_name) {
        ApplicationRecorder.p2_name = o2_name;
    }

    public static String getP1_weight() {
        return p1_weight;
    }

    public static void setP1_weight(String p1_weight) {
        ApplicationRecorder.p1_weight = p1_weight;
    }

    public static String getP2_weight() {
        return p2_weight;
    }

    public static void setP2_weight(String p2_weight) {
        ApplicationRecorder.p2_weight = p2_weight;
    }

    public static int getDiff_score() {
        return diff_score;
    }

    public static void setDiff_score(int diff_score) {
        ApplicationRecorder.diff_score = diff_score;
    }

    public static int getTime_all() {
        return time_all;
    }

    public static void setTime_all(int time_all) {
        ApplicationRecorder.time_all = time_all;
    }
}
