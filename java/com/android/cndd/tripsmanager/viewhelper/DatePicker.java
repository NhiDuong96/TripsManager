package com.android.cndd.tripsmanager.viewhelper;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Minh Nhi on 3/7/2018.
 */

public class DatePicker extends android.support.v7.widget.AppCompatEditText
        implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "DatePicker";

    private Date date;
    private Context context;

    public DatePicker(Context context) {
        super(context);
        this.context = context;
        setOnFocusChangeListener(focusChangeListener);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOnFocusChangeListener(focusChangeListener);
    }

    public void addDate(Date date){
        this.date = date;
    }

    private OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
        if(hasFocus){
            Calendar c = Calendar.getInstance();
            DatePickerDialog picker = new DatePickerDialog(context, this,
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH) , c.get(Calendar.DAY_OF_MONTH));
            picker.show();
        }
    };


    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        setText(String.format(Locale.getDefault(), "%d-%d-%d", dayOfMonth, month, year));
        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
        date = calendar.getTime();
    }

    public static String convertToDate(Date date){
        return String.format(Locale.getDefault(), "%d-%d-%d", date.getDay(), date.getMonth(), date.getYear());
    }
}
