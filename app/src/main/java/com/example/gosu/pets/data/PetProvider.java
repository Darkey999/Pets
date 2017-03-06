package com.example.gosu.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.gosu.pets.R;
import com.example.gosu.pets.data.PetContract.PetEntry;

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
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case PET_ID:
                // "_id=?"
                selection = PetEntry._ID + "=?";
                // Get ID from URI
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                Toast.makeText(getContext(), "Cannot query unknown URI" + uri, Toast.LENGTH_SHORT).show();
                return null;

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
                Toast.makeText(getContext(), "Cannot query unknown URI " + uri, Toast.LENGTH_SHORT).show();
                return null;
        }
    }

    // Insert new pet, used in .insert()
    private Uri insertPet(Uri uri, ContentValues contentValues) {
        // Check validity of the data
        String name = contentValues.getAsString(PetEntry.COLUMN_PET_NAME);
        Integer gender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        Integer weight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), R.string.pet_name_required, Toast.LENGTH_SHORT).show();
            return null;
        }

        if (gender == null || (gender != PetEntry.GENDER_UNKNOWN && gender != PetEntry.GENDER_MALE
                && gender != PetEntry.GENDER_FEMALE)) {
            Toast.makeText(getContext(), R.string.pet_gender_required, Toast.LENGTH_SHORT).show();
            return null;
        }

        if (weight != null && weight < 0) {
            Toast.makeText(getContext(), R.string.pet_weight_required, Toast.LENGTH_SHORT).show();
            return null;
        }

        SQLiteDatabase database = petHelper.getWritableDatabase();

        // Insert new pet
        long id = database.insert(PetEntry.TABLE_NAME, null, contentValues);

        // Problem with insertion
        if (id != -1) {
            Log.e("Failed to insert for", String.valueOf(uri));
        }

        return ContentUris.withAppendedId(uri, id);
    }

    // Updates the data at the given selection and selection arguments
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        // Check the URI
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                Toast.makeText(getContext(), "Update is not supported for " + uri, Toast.LENGTH_SHORT).show();
                return 0;
        }
    }

    // Update existing pet, used in .update()
    private int updatePet(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // Check the name
        if (contentValues.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = contentValues.getAsString(PetEntry.COLUMN_PET_NAME);
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), R.string.pet_name_required, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        // Check gender
        if (contentValues.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null || (gender != PetEntry.GENDER_UNKNOWN && gender != PetEntry.GENDER_MALE
                    && gender != PetEntry.GENDER_FEMALE)) {
                Toast.makeText(getContext(), R.string.pet_gender_required, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        // Check weight
        if (contentValues.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                Toast.makeText(getContext(), R.string.pet_weight_required, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        // Don't update if there is nothing new
        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = petHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        return database.update(PetEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    // Delete the data at the given selection and selection arguments
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = petHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        // Check the URI
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                Toast.makeText(getContext(), "Deletion is not supported for: " +
                        uri, Toast.LENGTH_SHORT).show();
                return 0;
        }
    }

    // Returns the type of data for the content URI
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        // Check the URI
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                Toast.makeText(getContext(), "Unknown URI " + uri + " with match " +
                        match, Toast.LENGTH_SHORT).show();
                return null;
        }
    }
}
