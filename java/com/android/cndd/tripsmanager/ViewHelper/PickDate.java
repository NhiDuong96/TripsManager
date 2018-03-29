package com.android.cndd.tripsmanager.ViewHelper;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Minh Nhi on 3/7/2018.
 */

public class PickDate{
    private FragmentActivity fragmentActivity;
    private EditText ui;
    private Date date;

    private PickDate(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public static PickDate from(FragmentActivity fragmentActivity){
        return new PickDate(fragmentActivity);
    }

    public void setPickDate(EditText ui, Date date){
        ui.setOnFocusChangeListener(mDateClick);
        this.ui = ui;
        this.date = date;
    }
    public void setPickTime(EditText ui, Date date){
        ui.setOnFocusChangeListener(mTimeClick);
        this.ui = ui;
        this.date = date;
    }
    private View.OnFocusChangeListener mDateClick = (v, hasFocus) -> {
        if(hasFocus) {
            DatePickerFragment datePicker = new DatePickerFragment();
            datePicker.show(fragmentActivity.getSupportFragmentManager(), "datePicker");
            datePicker.setOnDateSelectedListener((year, month, day) -> {
                ui.setText(String.format(Locale.getDefault(), "%d-%d-%d", day, month, year));
                date.setYear(year);
                date.setMonth(month);
                date.setDate(day);
            });
        }
    };
    private View.OnFocusChangeListener mTimeClick = (v, hasFocus) -> {
        if(hasFocus) {
            TimePickerFragment timePicker = new TimePickerFragment();
            timePicker.show(fragmentActivity.getSupportFragmentManager(), "timePicker");
            timePicker.setOnTimeSelectedListener((hourOfday, minute) -> {
                ui.setText(String.format(Locale.getDefault(), "%d:%d", hourOfday, minute));
                date.setHours(hourOfday);
                date.setMinutes(minute);
            });
        }
    };

    public static String convertToDate(Date date){
        return String.format(Locale.getDefault(), "%d-%d-%d", date.getDay(), date.getMonth(), date.getYear());
    }

    public static String convertToTime(Date date){
        return String.format(Locale.getDefault(), "%d:%d", date.getHours(), date.getMinutes());
    }
}
