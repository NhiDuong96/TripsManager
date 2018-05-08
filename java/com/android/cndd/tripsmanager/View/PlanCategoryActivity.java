package com.android.cndd.tripsmanager.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.android.cndd.tripsmanager.model.PlanCategory;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.viewhelper.PlanCategoryAdapter;

/**
 * Created by Minh Nhi on 3/8/2018.
 */

public class PlanCategoryActivity extends AppCompatActivity {
    public static final String TAG = "PlanCategoryActivity";
    public class PlanName{
        Class<?> categories;
        PlanCategory plan;
        public PlanName(Class<?> categories, PlanCategory plan){
            this.categories = categories;
            this.plan = plan;
        }

        @Override
        public String toString() {
            return plan.toString();
        }
    }
    private PlanName[] planNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plans_category_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        planNames = new PlanName[]{
                new PlanName(null, PlanCategory.Flight),
                new PlanName(FlightCreateActivity.class, PlanCategory.Flight),
                new PlanName(CarRentalCreateActivity.class, PlanCategory.CarRental),
                new PlanName(RailCreateActivity.class, PlanCategory.Rail),
                new PlanName(null, PlanCategory.Meeting),
                new PlanName(ActivityCreateActivity.class, PlanCategory.Meeting),
                new PlanName(ActivityCreateActivity.class, PlanCategory.Activity),
                new PlanName(RestaurantCreateActivity.class, PlanCategory.Restaurant),
                new PlanName(null, PlanCategory.Map),
                new PlanName(ActivityCreateActivity.class, PlanCategory.Map),
                new PlanName(ActivityCreateActivity.class, PlanCategory.Directions),
                new PlanName(ActivityCreateActivity.class, PlanCategory.Note),
        };
        ListView listView = findViewById(R.id.list_plan);
        listView.setAdapter(new PlanCategoryAdapter(this,android.R.layout.simple_list_item_1
                ,planNames, R.layout.plan_category_header_layout));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.putExtra("class",planNames[position].categories);
            intent.putExtra("type",planNames[position].plan);

            setResult(RESULT_OK,intent);
            finish();
        });

    }
}
