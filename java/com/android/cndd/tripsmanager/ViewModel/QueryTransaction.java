package com.android.cndd.tripsmanager.viewmodel;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Minh Nhi on 3/23/2018.
 */

public class QueryTransaction {
    private static final String TAG = "QueryTransaction";
    public static final int INSERT = 1;
    public static final int DELETE = 2;
    public static final int UPDATE = 3;

    private final Stack<Query> queryStack;
    private final List<Query> queryList;

    private static QueryTransaction transaction;

    private QueryTransaction() {
        queryStack = new Stack<>();
        queryList = new ArrayList<>();
    }

    public static QueryTransaction getTransaction(){
        if(transaction == null){
            transaction = new QueryTransaction();
        }
        return transaction;
    }


    public QueryTransaction newOperation(int operator, ViewModel model, Object obj){
        Query query;
        switch (operator){
            case INSERT:

                query = new Query.InsertOperation(model,obj);
                break;
            case DELETE:
                query = new Query.DeleteOperation(model,obj);
                break;
            case UPDATE:
                query = new Query.UpdateOperation(model, obj);
                break;
            default:
                query = new Query(model, obj);
                break;
        }
        queryList.add(query);
        return this;
    }

    public void undo(){
        Query query = queryStack.pop();
        query.undoOperation();
    }

    public void execute(){
        for(Query query: queryList) {
            if (query.doOperation()) {
                queryStack.push(query);
            }
        }
        queryList.clear();
    }

    @SuppressLint("StaticFieldLeak")
    public void execute(IProgressQueryObserver observer){
          new AsyncTask<Void, Void, Void>() {
              @Override
              protected void onPreExecute() {
                  super.onPreExecute();
                  observer.onStartQuery();
              }

              @Override
            protected Void doInBackground(Void... voids) {
                  SystemClock.sleep(500);
                  for(Query query: queryList) {
                      if (query.doOperation()) {
                          observer.onSuccessQuery(query);
                          queryStack.push(query);
                      } else {
                          observer.onFailQuery(query);
                      }
                  }
                return null;
            }

              @Override
              protected void onPostExecute(Void aVoid) {
                  super.onPostExecute(aVoid);
                  observer.onEndQuery();
                  queryList.clear();
              }
          }.execute();
    }

}
