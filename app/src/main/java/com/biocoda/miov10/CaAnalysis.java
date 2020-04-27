package com.biocoda.miov10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.biocoda.miov10.data.DatabaseHandler;
import com.biocoda.miov10.util.Global;

public class CaAnalysis extends AppCompatActivity {

    private TextView totalIncomeTV, wagesTV, otherIncomeTV, totalSpendingTV;
    private TextView foodShoppingTV, otherShoppingTV, entertainmentTV, travelTV;
    private TextView accBalanceTV, totalSavingTV, miscTV, householdBillsTV;
    private ImageButton goToTransactions, goToDashboard;
    private DatabaseHandler caDBH;
    private double sumWg, sumOi, sumHb, sumFs, sumOs, sumEn ,sumTr, sumMi, sumSa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUITheme();
        setContentView(R.layout.activity_ca_analysis);

        totalIncomeTV = findViewById(R.id.totalIncomeTVXML);
        wagesTV = findViewById(R.id.wageTVXML);
        otherIncomeTV = findViewById(R.id.otherIncomeAmountTVXML);
        totalSpendingTV = findViewById(R.id.totalExpAmountTVXML);
        householdBillsTV = findViewById(R.id.billsTVXML);
        foodShoppingTV = findViewById(R.id.foodShoppingTVXML);
        otherShoppingTV = findViewById(R.id.otherShoppingTVXML);
        entertainmentTV = findViewById(R.id.entertainmentTVXML);
        travelTV = findViewById(R.id.travelTVXML);
        miscTV = findViewById(R.id.miscTVXML);
        totalSavingTV = findViewById(R.id.totalSavingTVXML);
        accBalanceTV = findViewById(R.id.accBalanceTVXML);

        caDBH = new DatabaseHandler(this);

        // get type totals from database handler class
        sumWg = caDBH.getTypeTotal("wage");
        sumOi = caDBH.getTypeTotal("other income");
        sumHb = caDBH.getTypeTotal("household bill");
        sumFs = caDBH.getTypeTotal("food shopping");
        sumOs = caDBH.getTypeTotal("other shopping");
        sumEn = caDBH.getTypeTotal("entertainment");
        sumTr = caDBH.getTypeTotal("travel");
        sumMi = caDBH.getTypeTotal("miscellaneous");
        sumSa = caDBH.getTypeTotal("saving account");

        // send double outputs to TextView formatter
        tvFormatter(sumSa, totalSavingTV);
        tvFormatter(sumWg, wagesTV);
        tvFormatter(sumOi, otherIncomeTV);
        tvFormatter(sumOi + sumWg, totalIncomeTV);
        tvFormatter(sumHb, householdBillsTV);
        tvFormatter(sumFs, foodShoppingTV);
        tvFormatter(sumOs, otherShoppingTV);
        tvFormatter(sumEn, entertainmentTV);
        tvFormatter(sumTr, travelTV);
        tvFormatter(sumMi, miscTV);
        tvFormatter(sumHb + sumFs + sumOs + sumEn + sumTr + sumMi + sumSa, totalSpendingTV);
        double sumSpending = sumHb + sumFs + sumOs + sumEn + sumTr + sumMi + sumSa;
        double sumIncome = sumOi + sumWg;
        tvFormatter((sumIncome - sumSpending), accBalanceTV);

        // go to transaction view button
        goToTransactions = findViewById(R.id.transactionIB);
        goToTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToTransactions = new Intent(CaAnalysis.this, CaTransactions.class);
                startActivity(goToTransactions);
            }
        });

        // go to dashboard button
        goToDashboard = findViewById(R.id.goToDashboardIBXML);
        goToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDashBoard = new Intent(CaAnalysis.this, DashboardActivity.class);
                startActivity(goToDashBoard);
            }
        });
    }

    // format double to string and set TextView
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
