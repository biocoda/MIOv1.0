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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.biocoda.miov10.data.DatabaseHandler;
import com.biocoda.miov10.model.User;
import com.biocoda.miov10.util.Global;

import static com.biocoda.miov10.util.Global.isUserLoggedIn;

public class LoginActivity extends AppCompatActivity {

    private EditText userLoginEmailAddress, userLoginPassword;
    private Button userLoginButton;
    private TextView goRegisterBTV;

    @Override
    public void onResume() {
        super.onResume();
        setUITheme();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUITheme();
        setContentView(R.layout.activity_login);

        if (isUserLoggedIn) {
            Intent openLoginActivity = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(openLoginActivity);
        }

        final DatabaseHandler user_db = new DatabaseHandler(LoginActivity.this);

        Log.d("stu51272", "How many records in user table: " + user_db.getCount());
        Log.d("stu51272", "How many records in transaction table: " + user_db.getCATcount());

        userLoginEmailAddress = findViewById(R.id.loginEmailEntryField);
        userLoginPassword = findViewById(R.id.loginPasswordEntryField);
        userLoginButton = findViewById(R.id.loginButton);
        goRegisterBTV = findViewById(R.id.registerHereTVXML);

        SharedPreferences lastEmail = getSharedPreferences(Global.KEY_PREFS_EMAIL, Context.MODE_PRIVATE);
        String lastEmailToLogin = lastEmail.getString(Global.PREFS_EMAIL, "");
        userLoginEmailAddress.setText(lastEmailToLogin);

        goRegisterBTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRegisterActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(openRegisterActivity);
            }
        });

        userLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String emailFromLoginField = userLoginEmailAddress.getText().toString();
            String passwordFromLoginField = userLoginPassword.getText().toString();
            String storedUserEmail = user_db.Exist(emailFromLoginField);

            if ((emailFromLoginField.isEmpty()) || (passwordFromLoginField.isEmpty())) {
                Toast.makeText(getApplicationContext(), "Please enter all login details", Toast.LENGTH_LONG).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailFromLoginField).matches()) {
                Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_LONG).show();
            } else if (!(emailFromLoginField.equals(storedUserEmail))) {
                Toast.makeText(getApplicationContext(), "Email address not recognised", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences lastLoginEm = getSharedPreferences(Global.KEY_PREFS_EMAIL, Context.MODE_PRIVATE);
                lastLoginEm.edit().putString(Global.PREFS_EMAIL, emailFromLoginField).apply();
                User fromUsers_db = user_db.getUserDetailsFromEmail(emailFromLoginField);
                if (fromUsers_db.getPassword().equals(passwordFromLoginField)) {
                    loginSuccess();
                } else {
                    Toast.makeText(getApplicationContext(), "Password incorrect", Toast.LENGTH_LONG).show();
                }
            }
            }
        });
    }
    public void loginSuccess() {
        Log.d("stu51272", "Login successful");
        Intent goToDashboard = new Intent(LoginActivity.this, DashboardActivity.class);
        Global.isUserLoggedIn = true;
        startActivity(goToDashboard);
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

