package com.example.gosu.pets;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.gosu.pets.R;
import com.example.gosu.pets.data.PetContract;

// Adapter for the ListView in CatalogActivity
public class PetCursorAdapter extends CursorAdapter {
    private TextView tvName, tvBreed;

    // Constructor
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // Creating new View
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item, parent, false);
    }

    // Using already created View
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvBreed = (TextView) view.findViewById(R.id.tvBreed);

        // Get columns indexes
        int nameColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);

        // Get String from chosen column
        String petName = cursor.getString(nameColumnIndex);
        String breedName = cursor.getString(breedColumnIndex);

        // Set text to the TextViews
        tvName.setText(petName);
        tvBreed.setText(breedName);

    }
}
