package com.android.cndd.tripsmanager.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.android.cndd.tripsmanager.EntityDao.DBContext;

/**
 * Created by Minh Nhi on 3/23/2018.
 */

public abstract class ViewModel extends AndroidViewModel {
    private static final String TAG = "ViewModel";
    private DBContext mDBContext;

    public ViewModel(@NonNull Application application) {
        super(application);
        mDBContext = DBContext.getDBContext(application);
    }

    public DBContext getDBContext() {
        return mDBContext;
    }

    public abstract void Insert(Object obj);
    public abstract void Delete(Object obj);
    public abstract void Update(Object obj);

    public UpdateUIListener getUpdateUIListener(){
        return null;
    }
}
