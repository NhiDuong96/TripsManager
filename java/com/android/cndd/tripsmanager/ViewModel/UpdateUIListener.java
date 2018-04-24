package com.android.cndd.tripsmanager.ViewModel;

/**
 * Created by Minh Nhi on 3/23/2018.
 */

public interface UpdateUIListener {
    void onInsert(Query query, QueryArg args);
    void onDelete(Query query, QueryArg args);
    void onUpdate(Query query, QueryArg args);
    boolean isReadyForUpdate();
}
