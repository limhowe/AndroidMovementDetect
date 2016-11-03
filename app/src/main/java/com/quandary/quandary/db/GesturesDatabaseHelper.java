package com.quandary.quandary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lim on 11/3/16.
 */

public class GesturesDatabaseHelper extends SQLiteOpenHelper {

    private static String TAG = "GesturesDatabaseHelper";

    private static GesturesDatabaseHelper sInstance;

    private static final String DATABASE_NAME = "guestureDatabase";
    private static final int DATABASE_VERSION = 1;


    // Table Names
    private static final String TABLE_GUESTURE = "gesture";

    // Post Table Columns
    private static final String KEY_GESTURE_ID = "id";
    private static final String KEY_PACKAGE_NAME = "packageName";
    private static final String KEY_GESTURE_ACTION = "action";
    private static final String KEY_STATUS = "status";


    // ...

    public static synchronized GesturesDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new GesturesDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private GesturesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GUESTURES_TABLE = "CREATE TABLE " + TABLE_GUESTURE +
                "(" +
                KEY_GESTURE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Define a primary key
                KEY_GESTURE_ACTION + " TEXT," +
                KEY_PACKAGE_NAME + " TEXT," +
                KEY_STATUS + " INTEGER DEFAULT 0" +
                ")";

        db.execSQL(CREATE_GUESTURES_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUESTURE);
            onCreate(db);
        }
    }


    //CRUD METHODS

    public List<FliiikGesture> getAllGestures() {
        List<FliiikGesture> posts = new ArrayList<>();

        // SELECT * FROM GESTURES
        String GESTURES_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_GUESTURE);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(GESTURES_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    FliiikGesture newGesture = new FliiikGesture();
                    newGesture.id = cursor.getInt(cursor.getColumnIndex(KEY_GESTURE_ID));
                    newGesture.action = cursor.getString(cursor.getColumnIndex(KEY_GESTURE_ACTION));
                    newGesture.packageName = cursor.getString(cursor.getColumnIndex(KEY_PACKAGE_NAME));
                    newGesture.status = cursor.getInt(cursor.getColumnIndex(KEY_STATUS)) ==1;

                    posts.add(newGesture);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get gestures from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return posts;
    }


    // Insert a gesture into the database
    public void addGesture(FliiikGesture gesture) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_GESTURE_ACTION, gesture.action);
            values.put(KEY_PACKAGE_NAME, gesture.packageName);
            values.put(KEY_STATUS, gesture.status);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_GUESTURE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add gesture to database");
        } finally {
            db.endTransaction();
        }
    }

    // Update the gesture
    public int updateGesture(FliiikGesture gesture) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PACKAGE_NAME, gesture.packageName);
        values.put(KEY_STATUS, gesture.status);

        // Updating profile picture url for user with that userName
        return db.update(TABLE_GUESTURE, values, KEY_GESTURE_ID + " = ?",
                new String[] { String.valueOf(gesture.id) });
    }

    public void deleteGesture(FliiikGesture gesture) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_GUESTURE, KEY_GESTURE_ID + "=" + gesture.id, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all gestures and users");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAllGestures() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_GUESTURE, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all gestures and users");
        } finally {
            db.endTransaction();
        }
    }
}
