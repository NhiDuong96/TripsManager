package com.android.cndd.tripsmanager.ViewModel;

/**
 * Created by Minh Nhi on 3/23/2018.
 */

public abstract class Query<T> {

    class QueryArg{
        Object[] args;
        QueryArg(Object... objs){
            args = objs;
        }
    }

    protected QueryArg args;
    protected final ViewModel<T> model;
    protected final T obj;
    public int CODE = -1;

    protected IQueryUIObserver<T> uiObserver;

    public Query(ViewModel<T> model, T obj) {
        this.model = model;
        this.obj = obj;
    }

    public T getObj(){
        return obj;
    }

    public abstract boolean doOperation();
    public abstract boolean undoOperation();

    public void response(IQueryUIObserver<T> observer){
        this.CODE = -1;
    }

    public void setUpdateUIListener(IQueryUIObserver<T> uiObserver){
        this.uiObserver = uiObserver;
    }

    public void setUpdateUICode(int code){
        this.CODE = code;
    }

    public void setArguments(Object ... objects){
        args = new QueryArg(objects);
    }

    public QueryArg getArguments(){
        return args;
    }
}
