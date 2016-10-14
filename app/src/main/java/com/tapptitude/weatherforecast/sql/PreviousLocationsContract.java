package com.tapptitude.weatherforecast.sql;

import android.provider.BaseColumns;

/**
 * Created by ambroziepaval on 10/13/16.
 */
public final class PreviousLocationsContract {
    private PreviousLocationsContract() {
    }

    public static class PreviousLocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "previous_locations";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + PreviousLocationEntry.TABLE_NAME + " (" +
                    PreviousLocationEntry._ID + " INTEGER PRIMARY KEY," +
                    PreviousLocationEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    PreviousLocationEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    PreviousLocationEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + " )";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + PreviousLocationEntry.TABLE_NAME;
}
