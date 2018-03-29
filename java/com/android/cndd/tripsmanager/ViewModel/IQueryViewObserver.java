package com.android.cndd.tripsmanager.ViewModel;

/**
 * Created by Minh Nhi on 3/23/2018.
 */

public interface IQueryViewObserver {
    void onStartQuery();
    void onEndQuery();
    void onSuccessQuery(Query query);
    void onFailQuery(Query query);
}
