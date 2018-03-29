package com.android.cndd.tripsmanager.ViewModel;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Minh Nhi on 3/23/2018.
 */

public class QueryTransaction {

    private Stack<Query> queryStack;
    private static QueryTransaction transaction;

    private QueryTransaction() {
        queryStack = new Stack<>();
    }

    public static QueryTransaction getTransaction(){
        if(transaction == null){
            transaction = new QueryTransaction();
        }
        return transaction;
    }
    public void undo(){
        Query query = queryStack.pop();
        query.undoOperation();
    }

    public void execOnMainThread(Query query){
        if(query.doOperation()){
            queryStack.push(query);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void execOnBackground(Query query, IQueryViewObserver observer){
        if (observer == null) return;
          new AsyncTask<Query, Void, Void>() {
              @Override
              protected void onPreExecute() {
                  super.onPreExecute();
                  observer.onStartQuery();
              }

              @Override
            protected Void doInBackground(Query... queries) {
                  SystemClock.sleep(500);
                  if(queries[0].doOperation()){
                      observer.onSuccessQuery(queries[0]);
                      queryStack.push(queries[0]);
                  }else{
                      observer.onFailQuery(queries[0]);
                  }
                return null;
            }

              @Override
              protected void onPostExecute(Void aVoid) {
                  super.onPostExecute(aVoid);
                  observer.onEndQuery();
              }
          }.execute(query);
    }

    public void requestForUpdate(IQueryUIObserver observer){
        if(observer == null) return;
        for(Query query : queryStack){
            if(query.CODE == observer.getUpdateUICode()){
                query.response(observer);
            }
        }
    }
}
