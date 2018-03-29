package com.android.cndd.tripsmanager.ViewModel;

/**
 * Created by Minh Nhi on 3/23/2018.
 */

public class QueryFactory{

    public static class DeleteOperation<T> extends Query<T> {
        public DeleteOperation(ViewModel<T> model, T obj) {
            super(model, obj);
        }

        @Override
        public boolean doOperation() {
            try {
                model.Delete(obj);
            }catch (Exception ex){
                return false;
            }
            if(uiObserver != null) response(uiObserver);
            return true;
        }

        @Override
        public void response(IQueryUIObserver<T> observer) {
            observer.onDelete(this, args);
            super.response(observer);
        }

        @Override
        public boolean undoOperation() {
            InsertOperation insertOperation = new InsertOperation<>(model, obj);
            return insertOperation.doOperation();
        }
    }

    public static class InsertOperation<T> extends Query<T> {
        public InsertOperation(ViewModel<T> model, T obj) {
            super(model, obj);
        }

        @Override
        public boolean doOperation() {
            try {
                model.Insert(obj);
            }catch (Exception ex){
                return false;
            }
            if(uiObserver != null)
                response(uiObserver);
            return true;
        }

        @Override
        public void response(IQueryUIObserver<T> observer) {
            observer.onInsert(this, args);
            super.response(observer);
        }

        @Override
        public boolean undoOperation() {
            DeleteOperation deleteOperation = new DeleteOperation<>(model, obj);
            return deleteOperation.doOperation();
        }
    }

    public static class UpdateOperation<T> extends Query<T>{

        public UpdateOperation(ViewModel<T> model, T obj) {
            super(model, obj);
        }

        @Override
        public boolean doOperation() {
            try {
                model.Update(obj);
            }catch (Exception ex){
                return false;
            }
            if(uiObserver != null)
                response(uiObserver);
            return true;
        }

        @Override
        public void response(IQueryUIObserver<T> observer) {
            observer.onUpdate(this, args);
            super.response(observer);
        }

        @Override
        public boolean undoOperation() {
            return false;
        }
    }
}
