package com.tapptitude.weatherforecast.sql;

import android.provider.BaseColumns;

/**
 * Created by ambroziepaval on 10/13/16.
 */
final class PreviousLocationsContract {
    private PreviousLocationsContract() {
    }

    static class PreviousLocationEntry implements BaseColumns {
        static final String TABLE_NAME = "previous_locations";
        static final String COLUMN_NAME_LOCATION = "location";
        static final String COLUMN_NAME_LONGITUDE = "longitude";
        static final String COLUMN_NAME_LATITUDE = "latitude";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + PreviousLocationEntry.TABLE_NAME + " (" +
                    PreviousLocationEntry._ID + " INTEGER PRIMARY KEY," +
                    PreviousLocationEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    PreviousLocationEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    PreviousLocationEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + " )";

    static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + PreviousLocationEntry.TABLE_NAME;
}
