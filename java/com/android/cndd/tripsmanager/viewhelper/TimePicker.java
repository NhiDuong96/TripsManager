package com.android.cndd.tripsmanager.viewhelper;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimePicker extends android.support.v7.widget.AppCompatEditText
        implements TimePickerDialog.OnTimeSetListener{


    private Context context;
    private Date date;

    public TimePicker(Context context) {
        super(context);
        this.context = context;
        setOnFocusChangeListener(focusChangeListener);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOnFocusChangeListener(focusChangeListener);
    }

    public void addTime(Date date){
        this.date = date;
    }

    private OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
        if(hasFocus){
            Calendar c = Calendar.getInstance();
            TimePickerDialog picker = new TimePickerDialog(context, this,
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(context));
            picker.show();
        }
    };

    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        setText(String.format(Locale.getDefault(), "%d:%d", hourOfDay, minute));
        date.setHours(hourOfDay);
        date.setMinutes(minute);
    }

    public static String convertToTime(Date date){
        return String.format(Locale.getDefault(), "%d:%d", date.getHours(), date.getMinutes());
    }
}
