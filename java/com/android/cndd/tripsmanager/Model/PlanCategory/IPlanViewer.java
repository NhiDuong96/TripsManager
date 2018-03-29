package com.android.cndd.tripsmanager.Model.PlanCategory;

/**
 * Created by Minh Nhi on 3/22/2018.
 */

public interface IPlanViewer {
    String toHtmlLayout();
    int getIConResId();
    String getCategoryName();
}
