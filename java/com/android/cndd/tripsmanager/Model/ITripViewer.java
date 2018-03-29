package com.android.cndd.tripsmanager.Model;

import java.io.Serializable;

public interface ITripViewer extends Serializable {
    int getId();

    String getIconUrl();

    String getTitle();

    String getTime();

    String getInterval();
}
