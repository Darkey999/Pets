package com.example.gosu.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.gosu.pets.data.PetContract.PetEntry;

public class CatalogActivity extends AppCompatActivity {
    private ListView lvItems;
    private PetCursorAdapter petAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        lvItems = (ListView) findViewById(R.id.lvItems);

        // Set up FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        displayDatabaseInfo();
    }

    // Inserts test data to the db
    private void InsertDummy() {
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);
        getContentResolver().insert(PetEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Clicking on an item from the overflow menu
        switch (item.getItemId()) {
            // "Insert dummy data" menu option clicked
            case R.id.action_insert_dummy_data:
                InsertDummy();
                displayDatabaseInfo();
                return true;
            // "Delete all entries" menu option clicked
            case R.id.action_delete_all_entries:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    // Shows name and breed of each pet
    private void displayDatabaseInfo() {

        // List of columns that I am interested in
        String[] projections = new String[]{PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT,};

        // Use projections tab to choose proper columns
        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI, projections, null, null, null);

        // Create new adapter
        petAdapter = new PetCursorAdapter(this, cursor);

        // Attach adapter to the ListView
        lvItems.setAdapter(petAdapter);

    }
}
