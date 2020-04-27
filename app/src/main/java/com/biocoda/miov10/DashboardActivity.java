package com.biocoda.miov10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.biocoda.miov10.data.DatabaseHandler;
import com.biocoda.miov10.model.Balance;
import com.biocoda.miov10.model.CATransactions;
import com.biocoda.miov10.util.Global;
import com.google.android.material.snackbar.Snackbar;

public class DashboardActivity extends AppCompatActivity {

    private ImageView sendMessageCurrAcc, sendMessageSavAcc, goToCatButton;
    private ImageButton goToSettings;
    private DatabaseHandler catDbHandler;
    private double sumCredit, sumDebit, sumSav, balanceDouble, cashLeftToSpend, totalAmountSpent;
    private TextView spTotalTV, saTotalTV, saTgtTV, spTgtTV, caBalTV, saBalTV;
    private ProgressBar savingPB, spendPB;
    private Balance cb;

    SharedPreferences firstRun = null;

    @Override
    public void onResume() {
        super.onResume();
        setUITheme();
        loadStaticDBValues();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUITheme();
        setContentView(R.layout.activity_main);

        caBalTV = findViewById(R.id.currAccBalanceTV);
        saBalTV = findViewById(R.id.savAccBalanceTV);
        spTotalTV = findViewById(R.id.spCurProgTVXML);
        saTotalTV = findViewById(R.id.saCurProgTVXML);
        saTgtTV = findViewById(R.id.saTgtProgTVXML);
        spTgtTV = findViewById(R.id.spTgtProgTVXML);
        spendPB = findViewById(R.id.spPBXML);
        savingPB = findViewById(R.id.saPBXML);
        catDbHandler = new DatabaseHandler(this);

        // load up dummy db values on first run
        firstRun = this.getSharedPreferences(Global.KEY_FIRST_RUN, Context.MODE_PRIVATE);
        loadStaticDBValues();

        // Display account balances
        tvFormatter(catDbHandler.getCurrAccTotal("credit")
                - (catDbHandler.getCurrAccTotal("debit")
                + catDbHandler.getCurrAccTotal("transfer to saving account")), caBalTV);
        tvFormatter(catDbHandler.getCurrAccTotal("debit")
                + catDbHandler.getCurrAccTotal("transfer to saving account"), spTotalTV);
        tvFormatter(catDbHandler.getCurrAccTotal("transfer to saving account"), saTotalTV);
        tvFormatter(catDbHandler.getCurrAccTotal("transfer to saving account"), saBalTV);


        // get user spend/save target values from shared preferences
        SharedPreferences setSpTgt = this.getSharedPreferences(Global.KEY_SPEND_TGT, Context.MODE_PRIVATE);
            String savedSpTgt = setSpTgt.getString(Global.SPENDING_TGT, "2000");
            double spTgtToSpendAsDouble = Double.parseDouble(savedSpTgt.replace(",", ""));
            tvFormatter(spTgtToSpendAsDouble, spTgtTV);
        SharedPreferences setSaTgt = this.getSharedPreferences(Global.KEY_SAVE_TGT, Context.MODE_PRIVATE);
            String savedSaTgt = setSaTgt.getString(Global.SAVE_TGT, "1000");
            double savedSaTgtAsDouble = Double.parseDouble(savedSaTgt.replace(",", ""));
            tvFormatter(savedSaTgtAsDouble, saTgtTV);

            cashLeftToSpend =  (spTgtToSpendAsDouble - catDbHandler.getCurrAccTotal("debit")) - catDbHandler.getCurrAccTotal("transfer to saving account");
            String formatCLTS = String.format("%,.2f", cashLeftToSpend);

            double spBarValue =   (   (catDbHandler.getCurrAccTotal("debit") + catDbHandler.getCurrAccTotal("transfer to saving account"))   /spTgtToSpendAsDouble)  * 100;
            spendPB.setProgress((int) spBarValue);

            double saBarValue = (catDbHandler.getCurrAccTotal("transfer to saving account")/savedSaTgtAsDouble) * 100;
            savingPB.setProgress((int) saBarValue);

            totalAmountSpent = catDbHandler.getCurrAccTotal("debit") + catDbHandler.getCurrAccTotal("transfer to saving account");

        SharedPreferences notifications = this.getSharedPreferences(Global.KEY_TARGET_NOTIFICATIONS, Context.MODE_PRIVATE);
        boolean notificationsOnOff = notifications.getBoolean(Global.TARGET_NOTIFICATIONS, true);

        if (notificationsOnOff) {
            if ((spBarValue > 50.00) && (spBarValue < 100)) {
                String spToastFormat = String.format("%,.0f", spBarValue);
                String spToastMessage = "you have spent " + spToastFormat+ "% of this months budget and have £" + formatCLTS + " left to spend";
                Toast toast= Toast.makeText(getApplicationContext(),spToastMessage, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 580);
                toast.show();
            } else if (spBarValue > 100) {
                Log.d("stu51272", "onCreate: " + totalAmountSpent);
                double overBudget = totalAmountSpent - spTgtToSpendAsDouble;
                String overBFormat = String.format("%,.2f", overBudget);
                String spToastMessage = "you have gone over your spending budget by £" + overBFormat;
                Toast toast= Toast.makeText(getApplicationContext(),spToastMessage, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 580);
                toast.show();
            }
            if ((saBarValue > 24.99) && (saBarValue <= 99.99)) {
                String saSnackbarFormat = String.format("%,.0f", saBarValue);
                Snackbar.make(findViewById(android.R.id.content), "You have achieved "+ saSnackbarFormat+"% of your saving target - keep going!", Snackbar.LENGTH_LONG).show();
            } else if (saBarValue > 99.99) {
                Snackbar.make(findViewById(android.R.id.content),"Saving target achieved well done!",Snackbar.LENGTH_LONG).show();
            }
        }

        // View transaction button
        goToCatButton = findViewById(R.id.goToTransBTNXML);
        goToCatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCaTransactions = new Intent(DashboardActivity.this, CaTransactions.class);
                startActivity(goToCaTransactions);
            }
        });

        // Sharing handlers
        sendMessageCurrAcc = findViewById(R.id.curAccSendMessage);
        sendMessageCurrAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String savAccNoExtra = " Re: Acc/No. 01182388 SrtCd. 05-02-23 ";
                Intent sendAnSMS = new Intent(DashboardActivity.this, SmsActivity.class);
                sendAnSMS.putExtra(Intent.EXTRA_TEXT, savAccNoExtra);
                startActivity(sendAnSMS);
            }
        });
        sendMessageSavAcc = findViewById(R.id.savAccSendMessage);
        sendMessageSavAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String savAccNoExtra = " Re: Acc/No. 87223831 SrtCd. 05-02-23 ";
                Intent sendAnSMS = new Intent(DashboardActivity.this, SmsActivity.class);
                sendAnSMS.putExtra(Intent.EXTRA_TEXT, savAccNoExtra);
                startActivity(sendAnSMS);
            }
        });

        // Settings button handler
        goToSettings = findViewById(R.id.settingIB);
        goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(DashboardActivity.this, SettingsActivity.class);
                startActivity(goToSettings);
            }
        });
    }

    private void tvFormatter(Double toFormat, TextView t) {
        String formattedString= String.format("%,.2f", toFormat);
        String formattedTv = "£ " + formattedString;
        t.setText(formattedTv);
    }

    public void composeMessage(String accNo) {
        Intent sendMessage = new Intent(Intent.ACTION_SEND);
        sendMessage.setType("text/plain");
        sendMessage.putExtra(Intent.EXTRA_SUBJECT, "RE: Account No: " + accNo);
        startActivity(sendMessage);
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

    // check if this is first time app has run
    public void loadStaticDBValues() {
        if (firstRun.getBoolean(Global.FIRST_RUN, true)) {
            setInitialDBValues();
            firstRun.edit().putBoolean(Global.FIRST_RUN, false).apply();
        }
    }

    // dummy transaction data
    public void setInitialDBValues() {
        CATransactions firstRun = new CATransactions();
        firstRun.setAmount("2250.00");
        firstRun.setCreditOrDebit("credit");
        firstRun.setType("wage");
        firstRun.setDescription("Wages");
        catDbHandler.addCAT(firstRun);
        firstRun.setAmount("86.54");
        firstRun.setCreditOrDebit("debit");
        firstRun.setType("household bill");
        firstRun.setDescription("Octopus Energy");
        catDbHandler.addCAT(firstRun);
        firstRun.setAmount("104.43");
        firstRun.setCreditOrDebit("debit");
        firstRun.setType("food shopping");
        firstRun.setDescription("Tesco");
        catDbHandler.addCAT(firstRun);
        firstRun.setAmount("697.50");
        firstRun.setCreditOrDebit("debit");
        firstRun.setType("household bill");
        firstRun.setDescription("Mortgage payment");
        catDbHandler.addCAT(firstRun);
        firstRun.setAmount("139.99");
        firstRun.setCreditOrDebit("debit");
        firstRun.setType("other shopping");
        firstRun.setDescription("JD Sports");
        catDbHandler.addCAT(firstRun);
        firstRun.setAmount("10.00");
        firstRun.setCreditOrDebit("credit");
        firstRun.setType("other income");
        firstRun.setDescription("Lottery Win");
        catDbHandler.addCAT(firstRun);
        firstRun.setAmount("150.00");
        firstRun.setCreditOrDebit("transfer to saving account");
        firstRun.setType("saving account");
        firstRun.setDescription("Transfer to savings");
        catDbHandler.addCAT(firstRun);
        firstRun.setAmount("6.75");
        firstRun.setCreditOrDebit("debit");
        firstRun.setType("travel");
        firstRun.setDescription("Network Rail");
        catDbHandler.addCAT(firstRun);
        firstRun.setAmount("18.50");
        firstRun.setCreditOrDebit("debit");
        firstRun.setType("entertainment");
        firstRun.setDescription("Vue Cinema");
        catDbHandler.addCAT(firstRun);
        catDbHandler.close();
        Log.d("stu51272", "Dummy Database loaded");
    }
}
