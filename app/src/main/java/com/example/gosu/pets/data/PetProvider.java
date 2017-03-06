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
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);

        }

        return cursor;
    }

    // Insert new data into the provider
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        Log.d("dafaqdafaq0", String.valueOf(match));
        // Check the URI
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
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
