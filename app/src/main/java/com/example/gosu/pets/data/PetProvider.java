package com.example.gosu.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

// Content Provider for Pets app
public class PetProvider extends ContentProvider {
    private PetDbHelper petHelper;
    public static final int PETS = 100;
    public static final int PET_ID = 101;

    // Create a UriMatcher object ('s' stands for the 'static')
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Whole table
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        // Specified row
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    @Override
    public boolean onCreate() {
        petHelper = new PetDbHelper(getContext());
        return true;
    }

    // Perform the query for the given URI
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = petHelper.getReadableDatabase();
        Cursor cursor;
        final int match = sUriMatcher.match(uri);

        // Check the URI
        switch (match) {
            case PETS:
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case PET_ID:
                // "_id=?"
                selection = PetContract.PetEntry._ID + "=?";
                // Get ID from URI
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);

        }

        return cursor;
    }

    // Insert new data into the provider
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);

        // Check the URI
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
    }

    // Insert new pet, used in .insert()
    private Uri insertPet(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = petHelper.getWritableDatabase();
        long id = database.insert(PetContract.PetEntry.TABLE_NAME, null, contentValues);

        // Problem with insertion
        if (id != -1) {
            Log.e("Failed to insert for", String.valueOf(uri));
        }

        return ContentUris.withAppendedId(uri, id);
    }

    // Updates the data at the given selection and selection arguments
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    // Delete the data at the given selection and selection arguments
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    // Returns the type of data for the content URI
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
