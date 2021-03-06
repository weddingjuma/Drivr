/*
 * Copyright 2016 CMPUT301F16T10
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package ca.ualberta.cs.drivr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * An activity that displays information about a user's profile.
 */
public class ProfileActivity extends AppCompatActivity {

    private final UserManager userManager = UserManager.getInstance();
    private ProfileController profileController;

    private View profileContent;
    private ImageView editProfileImageView;
    private TextView notSignedInText;
    private EditText profileNameEditText;
    private EditText usernameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText vehicleEditText;
    private Button saveChanges;

    private ViewSwitcher usernameSwitch;
    private ViewSwitcher phoneSwitch;
    private ViewSwitcher emailSwitch;
    private ViewSwitcher profileNameSwitch;
    private ViewSwitcher vehicleSwitch;
    private TextView profileNameTextView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView vehicleTextView;

    private User user;
    private String email;
    private String name;
    private String username;
    private String phoneNumber;
    private String vehicleDescription;

    private Boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        usernameSwitch = (ViewSwitcher) findViewById(R.id.username_switcher);
        usernameTextView = (TextView) findViewById(R.id.profile_username_text_view);

        emailSwitch = (ViewSwitcher) findViewById(R.id.email_switcher);
        phoneSwitch = (ViewSwitcher) findViewById(R.id.phone_number_switcher);
        profileNameSwitch = (ViewSwitcher) findViewById(R.id.name_switcher);
        vehicleSwitch = (ViewSwitcher) findViewById(R.id.vehicle_description_switcher);

        emailTextView = (TextView) findViewById(R.id.profile_email_text_view);
        phoneTextView = (TextView) findViewById(R.id.profile_phone_number_text_view);
        profileNameTextView = (TextView) findViewById(R.id.profile_name_text_view);
        vehicleTextView = (TextView) findViewById(R.id.vehicle_description_text_view);


        // Get a reference to the content container
        profileContent = findViewById(R.id.profile_content);

        // Get references to the ImageViews
        editProfileImageView = (ImageView) findViewById(R.id.profile_edit_icon);

        // Get references to the TextViews
        notSignedInText = (TextView) findViewById(R.id.profile_not_signed_in_text);


        // Get references to the EditTexts
        profileNameEditText = (EditText) findViewById(R.id.profile_name_edit_text);
        usernameEditText = (EditText) findViewById(R.id.profile_username_edit_text);
        phoneEditText = (EditText) findViewById(R.id.profile_phone_number_edit_text);
        emailEditText = (EditText) findViewById(R.id.profile_email_edit_text);
        vehicleEditText = (EditText) findViewById(R.id.vehicle_description_edit_text);

        // Get the current user
        user = userManager.getUser();

        // Get info
        phoneNumber = user.getPhoneNumber();
        email = user.getEmail();
        username = user.getUsername();
        name = user.getName();
        vehicleDescription = user.getVehicleDescription();

        // Show the appropriate content based on whether the user is signed in or not
        toggleContent(username != null && !username.isEmpty());

        usernameSwitch.findViewById(R.id.profile_username_text_view);
        usernameTextView.setText(username);
        phoneTextView.setText(phoneNumber);
        emailTextView.setText(email);
        profileNameTextView.setText(name);

        CardView vehicleCard = (CardView) findViewById(R.id.vehicle_information_card);
        if (!vehicleDescription.isEmpty()) {
            vehicleTextView.setText(vehicleDescription);
            vehicleCard.setVisibility(View.VISIBLE);
        }

        editProfileImageView.setClickable(true);
        editProfileImageView.setOnClickListener(new View.OnClickListener(){
            @Override
                public void onClick(View v) {
                if (editMode) {
                    boolean changed = false;
                    Toast.makeText(getApplicationContext(), "Edit Data Mode OFF", Toast.LENGTH_SHORT).show();
                    editProfileImageView.setImageResource(R.drawable.ic_mode_edit_black_24dp);

                    if (!phoneEditText.getText().toString().equals(phoneNumber)) {
                        phoneNumber = phoneEditText.getText().toString();
                        user.setPhoneNumber(phoneNumber);
                        phoneTextView.setText(phoneNumber);
                        changed = true;

                    }

                    phoneSwitch.findViewById(R.id.profile_phone_number_text_view);
                    phoneEditText.setVisibility(View.GONE);
                    phoneTextView.setVisibility(View.VISIBLE);

                    if (!emailEditText.getText().toString().equals(email)) {
                        email = emailEditText.getText().toString();
                        user.setEmail(email);
                        emailTextView.setText(email);
                        changed = true;

                    }

                    emailSwitch.findViewById(R.id.profile_email_text_view);
                    emailEditText.setVisibility(View.GONE);
                    emailTextView.setVisibility(View.VISIBLE);

                    if (!vehicleEditText.getText().toString().equals(vehicleDescription)) {
                        vehicleDescription = vehicleEditText.getText().toString();
                        user.setVehicleDescription(vehicleDescription);
                        vehicleTextView.setText(vehicleDescription);
                        changed = true;

                    }

                    vehicleSwitch.findViewById(R.id.vehicle_description_text_view);
                    vehicleEditText.setVisibility(View.GONE);
                    vehicleTextView.setVisibility(View.VISIBLE);

                    if (!profileNameEditText.getText().toString().equals(name)) {
                        name = profileNameEditText.getText().toString();
                        user.setName(name);
                        profileNameTextView.setText(name);
                        changed = true;

                    }

                    if (changed) {
                        ElasticSearch elasticSearch = new ElasticSearch(userManager.getConnectivityManager());
                        elasticSearch.updateUser(userManager.getUser());
                        userManager.notifyObservers();
                    }
                    profileNameSwitch.findViewById(R.id.profile_name_text_view);
                    profileNameEditText.setVisibility(View.GONE);
                    profileNameTextView.setVisibility(View.VISIBLE);

                    editMode = false;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Edit Data Mode On", Toast.LENGTH_SHORT).show();
                    usernameEditText.setText(username);

                    phoneSwitch.findViewById(R.id.profile_phone_number_edit_text);
                    phoneEditText.setText(phoneNumber);
                    phoneEditText.setVisibility(View.VISIBLE);
                    phoneTextView.setVisibility(View.GONE);

                    emailSwitch.findViewById(R.id.profile_email_edit_text);
                    emailEditText.setText(email);
                    emailEditText.setVisibility(View.VISIBLE);
                    emailTextView.setVisibility(View.GONE);

                    profileNameSwitch.findViewById(R.id.profile_name_edit_text);
                    profileNameEditText.setText(name);
                    profileNameEditText.setVisibility(View.VISIBLE);
                    profileNameTextView.setVisibility(View.GONE);

                    vehicleSwitch.findViewById(R.id.vehicle_description_edit_text);
                    vehicleEditText.setText(vehicleDescription);
                    vehicleEditText.setVisibility(View.VISIBLE);
                    vehicleTextView.setVisibility(View.GONE);

                    editMode = true;
                    editProfileImageView.setImageResource(R.drawable.ic_check_black_24dp);
                }
            }
        });

    }

    /**
     * Shows and hides certain content based on whether the user is signed in orn not. When signed
     * in, the personal information of the user is shown. When not signed in, a message saying the
     * user is not signed in is shown.
     * @param isSignedIn True when signed in, false otherwise.
     */
    private void toggleContent(boolean isSignedIn) {
        // If there is not a user, display an empty page because there is no profile
        // The check for a user is if the username is non-null and non-empty
        if (!isSignedIn) {
            // There is no user signed in so display an empty profile page
            notSignedInText.setVisibility(View.VISIBLE);
            profileContent.setVisibility(View.GONE);
            editProfileImageView.setVisibility(View.GONE);
        }
        else {
            // There is a user, so make sure the page is not empty
            notSignedInText.setVisibility(View.GONE);
            profileContent.setVisibility(View.VISIBLE);
            editProfileImageView.setVisibility(View.VISIBLE);
        }
    }
}
