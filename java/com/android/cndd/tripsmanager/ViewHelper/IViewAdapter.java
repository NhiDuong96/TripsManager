package com.android.cndd.tripsmanager.ViewHelper;

/**
 * Created by Minh Nhi on 3/22/2018.
 */

public interface IViewAdapter<T> {
    T getItem(int position);
    int getIdItem(int position);
}
