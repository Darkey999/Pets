package com.example.gosu.pets;

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

import com.example.gosu.pets.data.PetContract.PetEntry;

public class EditorActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText breedEditText;
    private EditText weightEditText;
    private Spinner genderSpinner;
    private int gender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        nameEditText = (EditText) findViewById(R.id.edit_pet_name);
        breedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        weightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        genderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Adding menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Clicking on an item from the overflow menu
        switch (item.getItemId()) {
            // "Save" menu option clicked
            case R.id.action_save:

                return true;
            // "Delete" menu option clicked
            case R.id.action_delete:

                return true;
            // Arrow button clicked, navigating back to parent activity
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
