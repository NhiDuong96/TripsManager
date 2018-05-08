package com.android.cndd.tripsmanager.viewmodel;


import android.util.Log;

/**
 * Created by Minh Nhi on 3/23/2018.
 */

public class Query {
    private static final String TAG = "Query";
    protected final ViewModel model;
    protected final Object obj;

    public Query(ViewModel model, Object obj) {
        this.model = model;
        this.obj = obj;
    }

    public Object getObj(){
        return obj;
    }

    public boolean doOperation(){
        return true;
    }

    public boolean undoOperation(){
        return true;
    }

    //Operation

    public static class DeleteOperation extends Query {

        DeleteOperation(ViewModel model, Object obj) {
            super(model, obj);
        }

        @Override
        public boolean doOperation() {
            try {
                model.Delete(obj);
            }catch (Exception ex){
                Log.e(TAG, "doOperation: ", ex);
                return false;
            }
            return super.doOperation();
        }


        @Override
        public boolean undoOperation() {
            InsertOperation insertOperation = new InsertOperation(model, obj);
            return insertOperation.doOperation();
        }
    }

    public static class InsertOperation extends Query {

        InsertOperation(ViewModel model, Object obj) {
            super(model, obj);
        }

        @Override
        public boolean doOperation() {
            try {
                model.Insert(obj);
            }catch (Exception ex){
                Log.e(TAG, "doOperation: ", ex);
                return false;
            }
            return super.doOperation();
        }


        @Override
        public boolean undoOperation() {
            DeleteOperation deleteOperation = new DeleteOperation(model, obj);
            return deleteOperation.doOperation();
        }
    }

    public static class UpdateOperation extends Query{

        UpdateOperation(ViewModel model, Object obj) {
            super(model, obj);
        }

        @Override
        public boolean doOperation() {
            try {
                model.Update(obj);
            }catch (Exception ex){
                Log.e(TAG, "doOperation: ", ex);
                return false;
            }
            return super.doOperation();
        }

        @Override
        public boolean undoOperation() {
            return false;
        }
    }
}
