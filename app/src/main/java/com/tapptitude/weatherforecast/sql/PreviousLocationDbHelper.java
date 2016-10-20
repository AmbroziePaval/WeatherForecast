package com.tapptitude.weatherforecast.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by ambroziepaval on 10/13/16.
 */
public class PreviousLocationDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PreviousLocations.db";


    public PreviousLocationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PreviousLocationsContract.SQL_CREATE_TABLE);
    }

    public void clearTable(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL(PreviousLocationsContract.SQL_DELETE_TABLE);
        database.execSQL(PreviousLocationsContract.SQL_CREATE_TABLE);
        database.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PreviousLocationsContract.SQL_DELETE_TABLE);
        onCreate(db);
    }

    private boolean locationExists(String location) {
        SQLiteDatabase database = this.getReadableDatabase();

        String Query = "Select * from " + PreviousLocationsContract.PreviousLocationEntry.TABLE_NAME
                + " where " + PreviousLocationsContract.PreviousLocationEntry.COLUMN_NAME_LOCATION + " = \"" + location + "\"";
        Cursor cursor = database.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void insertLocation(String location, String longitude, String latitude) {
        if (!locationExists(location)) {
            SQLiteDatabase database = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(PreviousLocationsContract.PreviousLocationEntry.COLUMN_NAME_LOCATION, location);
            contentValues.put(PreviousLocationsContract.PreviousLocationEntry.COLUMN_NAME_LONGITUDE, longitude);
            contentValues.put(PreviousLocationsContract.PreviousLocationEntry.COLUMN_NAME_LATITUDE, latitude);

            database.insert(PreviousLocationsContract.PreviousLocationEntry.TABLE_NAME, null, contentValues);
            database.close();
        }
    }

    public List<MyLocation> getPreviousLocations() {
        List<MyLocation> previousLocations = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();

        String[] projection = {
                PreviousLocationsContract.PreviousLocationEntry._ID,
                PreviousLocationsContract.PreviousLocationEntry.COLUMN_NAME_LOCATION,
                PreviousLocationsContract.PreviousLocationEntry.COLUMN_NAME_LONGITUDE,
                PreviousLocationsContract.PreviousLocationEntry.COLUMN_NAME_LATITUDE
        };

        Cursor c = database.query(PreviousLocationsContract.PreviousLocationEntry.TABLE_NAME, projection, null, null, null, null, null);
        boolean entryLine = c.moveToFirst();
        while (entryLine) {
            previousLocations.add(
                    new MyLocation(c.getLong(c.getColumnIndex(PreviousLocationsContract.PreviousLocationEntry._ID)),
                            c.getString(c.getColumnIndex(PreviousLocationsContract.PreviousLocationEntry.COLUMN_NAME_LOCATION)),
                            c.getString(c.getColumnIndex(PreviousLocationsContract.PreviousLocationEntry.COLUMN_NAME_LONGITUDE)),
                            c.getString(c.getColumnIndex(PreviousLocationsContract.PreviousLocationEntry.COLUMN_NAME_LATITUDE))));
            entryLine = c.moveToNext();
        }
        c.close();
        database.close();

        return previousLocations;
    }
}
