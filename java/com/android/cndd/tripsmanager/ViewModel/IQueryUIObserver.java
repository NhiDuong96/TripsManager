package com.android.cndd.tripsmanager.ViewModel;

/**
 * Created by Minh Nhi on 3/23/2018.
 */

public interface IQueryUIObserver<T> {
    void onInsert(Query<T> query, Query.QueryArg args);
    void onDelete(Query<T> query, Query.QueryArg args);
    void onUpdate(Query<T> query, Query.QueryArg args);
    int getUpdateUICode();
}
