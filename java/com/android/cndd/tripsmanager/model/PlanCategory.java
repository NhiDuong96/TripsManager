package com.android.cndd.tripsmanager.model;


public enum PlanCategory {
    Flight("Flight"),
    CarRental("Car Rental"),
    Rail("Rail"),
    Meeting("Meeting"),
    Activity("Activity"),
    Restaurant("Restaurant"),
    Map("Map"),
    Directions("Directions"),
    Note("Note");
    private final String fieldDescription;

    PlanCategory(String alternativeName){
        fieldDescription = alternativeName;
    }

    @Override
    public String toString() {
        return fieldDescription;
    }
}
