package com.android.cndd.tripsmanager.viewhelper;

import android.view.View;

/**
 * Created by Minh Nhi on 3/22/2018.
 */

public interface IRemoveViewAnimation {
    Object getItem(int position);
    int getIdItem(int position);
    void removeItem(int position);
    void setOnTouchListener(View.OnTouchListener listener);
}
