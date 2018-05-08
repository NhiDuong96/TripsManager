package com.android.cndd.tripsmanager.viewmodel;

/**
 * Created by Minh Nhi on 3/23/2018.
 */

public interface IProgressQueryObserver {
    void onStartQuery();
    void onEndQuery();
    void onSuccessQuery(Query query);
    void onFailQuery(Query query);
}
