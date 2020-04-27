package com.biocoda.miov10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.biocoda.miov10.data.DatabaseHandler;
import com.biocoda.miov10.model.User;
import com.biocoda.miov10.util.Global;

public class RegisterActivity extends AppCompatActivity {
    private EditText userRegFirstName;
    private EditText userRegLastName;
    private EditText userRegEmailAddress;
    private EditText userRegPassword;
    private Button userRegButton;

    @Override
    public void onResume() {
        super.onResume();
        setUITheme();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUITheme();
        setContentView(R.layout.activity_register);

        final DatabaseHandler user_db = new DatabaseHandler(RegisterActivity.this);

        userRegFirstName = findViewById(R.id.firstNameRegEntryField);
        userRegLastName = findViewById(R.id.lastNameRegEntryField);
        userRegEmailAddress = findViewById(R.id.emailRegEntryField);
        userRegPassword = findViewById(R.id.passwordRegEntryField);
        userRegButton = findViewById(R.id.registerButton);

        // register button listener
        userRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // get values from input fields
            String firstNameFromRegField = userRegFirstName.getText().toString();
            String lastNameFromRegField = userRegLastName.getText().toString();
            String emailAddressFromRegField = userRegEmailAddress.getText().toString();
            String passwordFromRegField = userRegPassword.getText().toString();
            // check email address is unique to the database
            String storedUser = user_db.Exist(emailAddressFromRegField);

            // checking for empty/valid fields
            if ((firstNameFromRegField.isEmpty()) || (lastNameFromRegField.isEmpty()) || (emailAddressFromRegField.isEmpty()) || (passwordFromRegField.isEmpty())) {
                Toast.makeText(getApplicationContext(), "Please fill in all information", Toast.LENGTH_LONG).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddressFromRegField).matches()) {
                Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_LONG).show();
            } else if (emailAddressFromRegField.equals(storedUser)) {
                Toast.makeText(getApplicationContext(), "Email address already registered", Toast.LENGTH_SHORT).show();
            } else {
                User newRegUser = new User();
                    newRegUser.setFirstName(firstNameFromRegField);
                    newRegUser.setLastName(lastNameFromRegField );
                    newRegUser.setEmail(emailAddressFromRegField);
                    newRegUser.setPassword(passwordFromRegField);
                    newRegUser.setUiPreference("dark");
                user_db.addUser(newRegUser);
                user_db.close();
                // add email to shared prefs for display on login view
                SharedPreferences lastLoginEm = getSharedPreferences(Global.KEY_PREFS_EMAIL, Context.MODE_PRIVATE);
                lastLoginEm.edit().putString(Global.PREFS_EMAIL, emailAddressFromRegField).apply();
                // display welcome toast message
                Toast.makeText(getApplicationContext(), "Hi " + firstNameFromRegField + " - welcome to mIO", Toast.LENGTH_LONG).show();
                Intent goToLoginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(goToLoginActivity);
                finish();
            }
            }
        });
    }
    public void setUITheme() {
        SharedPreferences uiTheme = getSharedPreferences(Global.KEY_UI_PREFS, Context.MODE_PRIVATE);
        boolean savedSwitchState = uiTheme.getBoolean(Global.UI_PREFS, true);
        if (savedSwitchState) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
    }
}
