package com.example.dawn.appdesign.util;

import android.graphics.Color;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtil {

    Timer timer = new Timer();

    public void dealTextView (final TextView view, String changeTo, final String backTo, int time){
        view.setText(changeTo);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                view.setText(backTo);
            }
        },time);
    }

    public void dealColorView(final TextView view, int time){
        view.setBackgroundColor(Color.parseColor("#00FF00"));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                view.setBackgroundColor(Color.parseColor("#FF0000"));
            }
        },time);

    }


}
