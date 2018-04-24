package com.android.cndd.tripsmanager.ViewModel;


/**
 * Created by Minh Nhi on 3/23/2018.
 */

public class QueryFactory{

    public static final int INSERT = 1;
    public static final int DELETE = 2;
    public static final int UPDATE = 3;

    public static Query initOperation(int operator, ViewModel model, Object obj){
        switch (operator){
            case INSERT:
                return new Query.InsertOperation(model,obj);
            case DELETE:
                return new Query.DeleteOperation(model,obj);
            case UPDATE:
                return new Query.UpdateOperation(model, obj);
            default:
                return new Query(model, obj);
        }
    }
}
