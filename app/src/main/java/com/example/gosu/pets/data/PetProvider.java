package com.example.gosu.pets.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

// Content Provider for Pets app
public class PetProvider extends ContentProvider {
    private PetDbHelper petHelper;

    @Override
    public boolean onCreate() {
        petHelper = new PetDbHelper(getContext());
        return true;
    }

    // Perform the query for the given URI
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        return null;
    }

    // Insert new data into the provider
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
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
