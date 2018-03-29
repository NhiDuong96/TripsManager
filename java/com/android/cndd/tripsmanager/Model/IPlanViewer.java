package com.android.cndd.tripsmanager.Model;

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

    String getInformation();

    String toHtmlLayout();

    String getCategoryName();
}
