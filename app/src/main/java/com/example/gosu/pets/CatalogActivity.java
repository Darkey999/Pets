package com.example.gosu.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.gosu.pets.data.PetContract.PetEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView lvItems;
    private View emptyView;
    private PetCursorAdapter petAdapter;
    private ProgressBar pBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        lvItems = (ListView) findViewById(R.id.lvItems);
        emptyView = findViewById(R.id.empty_view);
        pBar = (ProgressBar) findViewById(R.id.pBar);

        // Set empty View
        lvItems.setEmptyView(emptyView);

        // Create and set CursorAdapter
        petAdapter = new PetCursorAdapter(this, null);
        lvItems.setAdapter(petAdapter);

        // Set item click listener, open EditorActivity
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                //Add URI of clicked pet "content://com.example.android.pets/pets/id"
                intent.setData(ContentUris.withAppendedId(PetEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });
        // Set up FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // Prepare the LoaderManager
        getLoaderManager().initLoader(0, null, this);
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
                return true;
            // "Delete all entries" menu option clicked
            case R.id.action_delete_all_entries:
                getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // CursorLoader onCreate
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // List of columns that I am interested in
        String[] projections = new String[]{
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED};

        return new CursorLoader(this, PetEntry.CONTENT_URI, projections, null, null, null);
    }

    // CursorLoader onLoadFinished
    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        petAdapter.swapCursor(data);
        pBar.setVisibility(View.GONE);
    }

    // CursorLoader onLoaderReset
    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        petAdapter.swapCursor(null);
    }
}
