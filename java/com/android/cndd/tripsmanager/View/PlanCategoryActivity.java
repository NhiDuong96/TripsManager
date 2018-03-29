package com.android.cndd.tripsmanager.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.cndd.tripsmanager.Model.Option.PlanCategories;
import com.android.cndd.tripsmanager.R;

/**
 * Created by Minh Nhi on 3/8/2018.
 */

public class PlanCategoryActivity extends AppCompatActivity {
    public static final String TAG = "PlanCategoryActivity";
    class PlanName{
        PlanCategories categories;
        String name;
        public PlanName(PlanCategories categories, String name){
            this.categories = categories;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    private PlanName[] travelNameList, activityNameList, otherNameList;
    private ListView travel, activity, other;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plans_category_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        travelNameList = new PlanName[]{
            new PlanName(PlanCategories.Flight, "Flight"),
            new PlanName(PlanCategories.CarRental, "Car Rental"),
            new PlanName(PlanCategories.Rail, "Rail")
        };
        travel = findViewById(R.id.list_travel);
        travel.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,travelNameList));
        travel.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.putExtra(TAG,travelNameList[position].categories.name());
            setResult(RESULT_OK,intent);
            finish();
        });

        //
        activityNameList = new PlanName[]{
                new PlanName(PlanCategories.Meeting, "Meeting"),
                new PlanName(PlanCategories.Activity, "Activity"),
                new PlanName(PlanCategories.Restaurant, "Restaurant")
        };
        activity = findViewById(R.id.list_activity);
        activity.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,activityNameList));
        activity.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.putExtra(TAG,activityNameList[position].categories.name());
            setResult(RESULT_OK,intent);
            finish();
        });

        //
        otherNameList = new PlanName[]{
                new PlanName(PlanCategories.Map, "Map"),
                new PlanName(PlanCategories.Directions, "Directions"),
                new PlanName(PlanCategories.Note, "Note"),
        };
        other = findViewById(R.id.list_other);
        other.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,otherNameList));
        other.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.putExtra(TAG,otherNameList[position].categories.name());
            setResult(RESULT_OK,intent);
            finish();
        });
    }
}
