package com.example.gosu.pets;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gosu.pets.data.PetContract.PetEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText nameEditText;
    private EditText breedEditText;
    private EditText weightEditText;
    private Spinner genderSpinner;
    private int gender = 0;
    private Intent intent;
    private Uri petUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        nameEditText = (EditText) findViewById(R.id.edit_pet_name);
        breedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        weightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        genderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();

        // Get Intent
        intent = getIntent();
        if (intent.getData() == null) {
            setTitle(R.string.editor_activity_title_new_pet);
        } else {
            setTitle(R.string.editor_activity_title_edit_pet);
            // Get URI of the pet
            petUri = intent.getData();
            // Prepare the LoaderManager
            getLoaderManager().initLoader(1, null, this);
        }
    }

    // Set up spinner, which allows to choose gender of the pet
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the xml String array
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        genderSpinner.setAdapter(genderSpinnerAdapter);

        // Select gender from the spinner
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        gender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        gender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        gender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = 0;
            }
        });
    }

    // Saves pet to the db
    private void savePet() {

        //Set values
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, nameEditText.getText().toString().trim());
        values.put(PetEntry.COLUMN_PET_BREED, breedEditText.getText().toString().trim());
        values.put(PetEntry.COLUMN_PET_GENDER, gender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, weightEditText.getText().toString().trim());
        Uri newUri = null;
        int rowUpdated = 0;

        // Check data
        if (intent.getData() == null) {
            newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
        } else {
            rowUpdated = getContentResolver().update(petUri, values, null, null);
        }

        // Result of the action
        if (newUri != null && intent.getData() == null) {
            Toast.makeText(EditorActivity.this, R.string.pet_add_toast, Toast.LENGTH_SHORT).show();
        } else if (rowUpdated != 0 && intent.getData() != null) {
            Toast.makeText(EditorActivity.this, R.string.pet_edit_toast, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(EditorActivity.this, R.string.pet_save_error_toast, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Adding menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        MenuItem menuItemDelete = menu.findItem(R.id.action_delete);

        // Remove 'delete' if adding new pet
        if (intent.getData() == null) {
            menuItemDelete.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Clicking on an item from the overflow menu
        switch (item.getItemId()) {

            // "Save" menu option clicked
            case R.id.action_save:
                savePet();
                finish();
                return true;

            // "Delete" menu option clicked
            case R.id.action_delete:
                AlertDialog.Builder alertDelete = new AlertDialog.Builder(this);
                alertDelete.setMessage(R.string.delete_pet_confirmation);
                alertDelete.setPositiveButton(R.string.delete_pet, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int rowDeleted = getContentResolver().delete(petUri, null, null);
                        if (rowDeleted == 0) {
                            Toast.makeText(EditorActivity.this, R.string.pet_not_deleted,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditorActivity.this, R.string.pet_deleted,
                                    Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
                alertDelete.setNegativeButton(R.string.cancel_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDelete.show();

                return true;

            // Arrow button clicked, navigating back to parent activity
            case android.R.id.home:

                // Check if views are empty
                if (!TextUtils.isEmpty(nameEditText.getText())
                        || !TextUtils.isEmpty(breedEditText.getText())
                        || !TextUtils.isEmpty(weightEditText.getText())) {
                    // Build alert dialog
                    AlertDialog.Builder alertHome = new AlertDialog.Builder(this);
                    alertHome.setMessage(R.string.discard_changes_confirmation);
                    alertHome.setPositiveButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            Toast.makeText(EditorActivity.this, R.string.changes_discarded,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertHome.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertHome.show();

                    return true;
                }
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle back button
    @Override
    public void onBackPressed() {

        // Check if views are empty
        if (!TextUtils.isEmpty(nameEditText.getText())
                || !TextUtils.isEmpty(breedEditText.getText())
                || !TextUtils.isEmpty(weightEditText.getText())) {
            // Build alert dialog
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(R.string.discard_changes_confirmation);
            alert.setPositiveButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(EditorActivity.this, R.string.changes_discarded,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            alert.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alert.show();
        } else {
            finish();
        }
    }

    // CursorLoader onCreate
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // List of columns that I am interested in
        String[] projections = new String[]{
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT};

        return new CursorLoader(this, petUri, projections, null, null, null);
    }

    // CursorLoader onLoadFinished
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Get columns indexes
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            // Get String from chosen columns
            String petName = cursor.getString(nameColumnIndex);
            String breedName = cursor.getString(breedColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            int weight = cursor.getInt(weightColumnIndex);

            //Set values
            nameEditText.setText(petName);
            breedEditText.setText(breedName);
            genderSpinner.setSelection(gender);
            weightEditText.setText(String.valueOf(weight));
        }
    }

    // CursorLoader onLoaderReset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        breedEditText.setText("");
        weightEditText.setText("");
        genderSpinner.setSelection(0);
    }
}
