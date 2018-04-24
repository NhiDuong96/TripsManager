package com.android.cndd.tripsmanager.ViewModel;


import android.util.Log;

/**
 * Created by Minh Nhi on 3/23/2018.
 */

public class Query {
    private static final String TAG = "Query";
    protected final ViewModel model;
    protected final Object obj;
    public boolean allowUpdateUi = true;
    QueryArg args;

    public Query(ViewModel model, Object obj) {
        this.model = model;
        this.obj = obj;
    }

    public Object getObj(){
        return obj;
    }

    public boolean doOperation(){
        UpdateUIListener update = model.getUpdateUIListener();
        if(update != null && update.isReadyForUpdate()) {
            updateUI(update);
        }
        return true;
    }

    public boolean undoOperation(){
        return true;
    }

    public void updateUI(UpdateUIListener listener){}

    public void allowQueryUpdateUi(boolean update){
        this.allowUpdateUi = update;
    }

    @Override
    public boolean equals(Object obj) {
        if(model.getUpdateUIListener() == null){
            return false;
        }
        return model.getUpdateUIListener().equals(obj);
    }

    public void setArguments(Object ... objects){
        args = new QueryArg(objects);
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
        public void updateUI(UpdateUIListener listener) {
            if(allowUpdateUi)
                listener.onDelete(this, args);
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
        public void updateUI(UpdateUIListener listener) {
            if(allowUpdateUi)
                listener.onInsert(this, args);
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
        public void updateUI(UpdateUIListener listener) {
            if(allowUpdateUi)
                listener.onUpdate(this, args);
        }

        @Override
        public boolean undoOperation() {
            return false;
        }
    }
}
