package com.android.cndd.tripsmanager.view;

import com.android.cndd.tripsmanager.model.PlanCategory;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Minh Nhi on 3/22/2018.
 */

public interface IPlanViewer extends Serializable {
    int getId();

    int getIconUrlId();

    String getDescription();

    String getTime();

    String getDate();

    LatLng getLatLng();

    String getInformation();

    String toHtmlLayout();

    PlanCategory getPlanCategoryName();
}
