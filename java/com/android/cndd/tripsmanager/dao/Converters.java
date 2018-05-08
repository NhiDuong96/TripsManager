package com.android.cndd.tripsmanager.dao;

import android.arch.persistence.room.TypeConverter;

import com.android.cndd.tripsmanager.model.PlanCategory;
import com.android.cndd.tripsmanager.model.Priority;
import com.android.cndd.tripsmanager.model.Status;

import java.util.Date;

/**
 * Created by Minh Nhi on 1/29/2018.
 */

public class Converters {
    public static class DateConverters{
        @TypeConverter
        public static Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }
    }

    public static class StatusConverters{
        @TypeConverter
        public static Status fromValue(String value){
            return value == null ? null : Status.valueOf(value);
        }
        @TypeConverter
        public static String statusToValue(Status status){
            return status == null ? null : status.name();
        }
    }

    public static class PriorityConverters{
        @TypeConverter
        public static Priority fromValue(String value){
            return value == null ? null : Priority.valueOf(value);
        }
        @TypeConverter
        public static String priorityToValue(Priority priority){
            return priority == null ? null : priority.name();
        }
    }

    public static class PlanCategoryConverters{
        @TypeConverter
        public static PlanCategory fromValue(String value){
            return value == null ? null : PlanCategory.valueOf(value);
        }
        @TypeConverter
        public static String planCategoryToValue(PlanCategory plan){
            return plan == null ? null : plan.name();
        }
    }
}
