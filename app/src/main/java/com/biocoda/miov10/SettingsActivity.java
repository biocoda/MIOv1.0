package com.biocoda.miov10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.biocoda.miov10.util.Global;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton dashboardIntent;
    private Switch uiSwitch, notificationsSWITCH;
    private AlertDialog.Builder setBuilder;
    private AlertDialog setDialog;
    private CardView spendingTargetCV, savingTargetCV;
    private EditText setSpendTargetET,setSaveTargetET;
    private Button addTargetBTN, cancelBTN;
    private TextView spendTargetTV, saveTargetTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUITheme();
        setContentView(R.layout.activity_settings);

        dashboardIntent = findViewById(R.id.dashboardIB);
        spendingTargetCV = findViewById(R.id.spendingTargetCVXML);
        savingTargetCV = findViewById(R.id.savingTargetCVXML);
        spendTargetTV = findViewById(R.id.spendTargetTVXML);
        saveTargetTV = findViewById(R.id.savingTargetTVXML);

        // set current view to current settings
        updateActivity();

        // listeners for settings dialog
        savingTargetCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSettingsDialog();
            }
        });
        spendingTargetCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSettingsDialog();
            }
        });

        // listener for UI switch position change
        uiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!uiSwitch.isChecked()) {
                    SharedPreferences uiTheme = getSharedPreferences(Global.KEY_UI_PREFS, Context.MODE_PRIVATE);
                    uiTheme.edit().putBoolean(Global.UI_PREFS, false).apply();
                    Log.d("stu51272", "onCheckedChanged: PREFS THEME set to AppThemeLight");
                    Toast uiToast = Toast.makeText(getApplicationContext(), "UI changed to light", Toast.LENGTH_SHORT);
                    uiToast.setGravity(Gravity.CENTER,0, 0);
                    uiToast.show();
                    startActivity(getIntent());
                    finish();
                } else {
                    SharedPreferences uiTheme = getSharedPreferences(Global.KEY_UI_PREFS, Context.MODE_PRIVATE);
                    uiTheme.edit().putBoolean(Global.UI_PREFS, true).apply();
                    Log.d("stu51272", "onCheckedChanged: PREFS THEME set to AppThemeDark");
                    Toast uiToast = Toast.makeText(getApplicationContext(), "UI changed to dark", Toast.LENGTH_SHORT);
                    uiToast.setGravity(Gravity.CENTER,0, 0);
                    uiToast.show();
                    startActivity(getIntent());
                    finish();
                }
            }
        });

        // listener for notifications switch position change
        notificationsSWITCH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!notificationsSWITCH.isChecked()) {
                    SharedPreferences notifications = getSharedPreferences(Global.KEY_TARGET_NOTIFICATIONS, Context.MODE_PRIVATE);
                    notifications.edit().putBoolean(Global.TARGET_NOTIFICATIONS, false).apply();
                    Log.d("stu51272", "onCheckedChanged: Notifications Off");
                    Toast notificationsToast = Toast.makeText(getApplicationContext(), "Notifications Off", Toast.LENGTH_SHORT);
                    notificationsToast.setGravity(Gravity.CENTER,0, 0);
                    notificationsToast.show();
                } else {
                    SharedPreferences notifications = getSharedPreferences(Global.KEY_TARGET_NOTIFICATIONS, Context.MODE_PRIVATE);
                    notifications.edit().putBoolean(Global.TARGET_NOTIFICATIONS, true).apply();
                    Log.d("stu51272", "onCheckedChanged: Notifications On");
                    Toast notificationsToast = Toast.makeText(getApplicationContext(), "Notifications On", Toast.LENGTH_SHORT);
                    notificationsToast.setGravity(Gravity.CENTER,0, 0);
                    notificationsToast.show();
                }
            }
        });

        // go to dashboard button
        dashboardIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDashboard = new Intent(SettingsActivity.this, DashboardActivity.class);
                startActivity(goToDashboard);
            }
        });
    }

    // gets current settings values and switch position when view starts
    public void updateActivity() {
        SharedPreferences uiTheme = this.getSharedPreferences(Global.KEY_UI_PREFS, Context.MODE_PRIVATE);
            boolean savedSwitchState = uiTheme.getBoolean(Global.UI_PREFS, true);
            uiSwitch = findViewById(R.id.switchUI);
            uiSwitch.setChecked(savedSwitchState);
        SharedPreferences notifications = this.getSharedPreferences(Global.KEY_TARGET_NOTIFICATIONS, Context.MODE_PRIVATE);
            boolean savedNotificationSwitchState = notifications.getBoolean(Global.TARGET_NOTIFICATIONS, true);
            notificationsSWITCH = findViewById(R.id.notificationSWXML);
            notificationsSWITCH.setChecked(savedNotificationSwitchState);
        SharedPreferences setSpTgt = this.getSharedPreferences(Global.KEY_SPEND_TGT, Context.MODE_PRIVATE);
            String savedSpTgt = setSpTgt.getString(Global.SPENDING_TGT, "2000");
            double spTgtToSpendAsDouble = Double.parseDouble(savedSpTgt.replace(",", ""));
            tvFormatter(spTgtToSpendAsDouble, spendTargetTV);
        SharedPreferences setSaTgt = this.getSharedPreferences(Global.KEY_SAVE_TGT, Context.MODE_PRIVATE);
            String savedSaTgt = setSaTgt.getString(Global.SAVE_TGT, "1000");
            double savedSaTgtAsDouble = Double.parseDouble(savedSaTgt.replace(",", ""));
            tvFormatter(savedSaTgtAsDouble, saveTargetTV);
    }

    // settings dialog
    public void createSettingsDialog() {
        setBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        View setAdView = getLayoutInflater().inflate(R.layout.settings_popup, null);

        cancelBTN = setAdView.findViewById(R.id.saveCaTBXML);
        addTargetBTN = setAdView.findViewById(R.id.saveTargetBXML);
        setSpendTargetET = setAdView.findViewById(R.id.addSpendTargetETXML);
        setSaveTargetET = setAdView.findViewById(R.id.addSavingTargetETXML);

        addTargetBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checks for empty fields
                if (setSpendTargetET.getText().toString().isEmpty() && (setSaveTargetET.getText().toString().isEmpty())) {
                    Toast.makeText(getApplicationContext(), "You have not entered any targets", Toast.LENGTH_SHORT).show();
                }
                if (!setSpendTargetET.getText().toString().isEmpty()) {
                    // get text from tv
                    String spTgtToSave = setSpendTargetET.getText().toString().trim();
                    // save to shared prefs
                    SharedPreferences setSpTgt = getSharedPreferences(Global.KEY_SPEND_TGT, Context.MODE_PRIVATE);
                    setSpTgt.edit().putString(Global.SPENDING_TGT, spTgtToSave).apply();
                    // convert to double and remove formatting
                    double spTgtToSaveAsDouble = Double.parseDouble(setSpendTargetET.getText().toString().replace(",", ""));
                    // output double to TV formatter
                    tvFormatter(spTgtToSaveAsDouble, spendTargetTV);
                }
                if (!setSaveTargetET.getText().toString().isEmpty()) {
                    String saTgtToSave = setSaveTargetET.getText().toString().trim();
                    SharedPreferences setSaTgt = getSharedPreferences(Global.KEY_SAVE_TGT, Context.MODE_PRIVATE);
                    setSaTgt.edit().putString(Global.SAVE_TGT, saTgtToSave).apply();
                    double saTgtToSaveAsDouble = Double.parseDouble(setSaveTargetET.getText().toString().replace(",", ""));
                    tvFormatter(saTgtToSaveAsDouble, saveTargetTV);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setDialog.dismiss();
                    }
                }, 300);
            }
        });
        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSetDialog();
            }
        });
        setBuilder.setView(setAdView);
        setDialog = setBuilder.create();
        setDialog.show();
    }

    public void closeSetDialog() {
        setDialog.dismiss();
    }

    // format the outputs and set the TextViews
    private void tvFormatter(Double toFormat, TextView t) {
        String formattedString= String.format("%,.2f", toFormat);
        String formattedTv = "£ " + formattedString;
        t.setText(formattedTv);
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

