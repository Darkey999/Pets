package com.example.gosu.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.gosu.pets.data.PetContract.PetEntry;

public class PetDbHelper extends SQLiteOpenHelper {
    public final static String DATABASE_NAME = "pets.db";
    public final static int DATABASE_VERSION = 1;
    private final static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;
    private final static String SQL_CREATE_ENTRIES = "CREATE TABLE " + PetEntry.TABLE_NAME +
            " (" + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, " +
            PetEntry.COLUMN_PET_BREED + " TEXT, " +
            PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, " +
            PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0 );";

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
