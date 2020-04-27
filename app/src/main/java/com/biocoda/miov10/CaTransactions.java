package com.biocoda.miov10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biocoda.miov10.data.DatabaseHandler;
import com.biocoda.miov10.model.CATransactions;
import com.biocoda.miov10.ui.RVadapter;
import com.biocoda.miov10.util.Global;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CaTransactions extends AppCompatActivity {

    private ImageButton goToDashboard, goToCaAnalysis;
    private AlertDialog.Builder caBuilder;
    private AlertDialog caDialog;
    private Spinner caDorcSpinner, caTypeSpinner;
    private EditText caPayeeEditText, caAmountEditText;
    private Button caSaveButton, cancelPopupButton;
    private DatabaseHandler catDbHandler;
    private RecyclerView rVCat;
    private RVadapter recyclerViewAdapter;
    private List<CATransactions> catTransList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUITheme();
        setContentView(R.layout.activity_ca_transactions);

        // find and instantiate recycler view
        rVCat = findViewById(R.id.rvCatXML);
        catDbHandler = new DatabaseHandler(this);
        rVCat.setHasFixedSize(true);
        rVCat.setLayoutManager(new LinearLayoutManager(this));
        refreshRV();

        // fab
        FloatingActionButton fab = findViewById(R.id.catAddFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCaDialog();
            }
        });

        // goto analysis button
        goToCaAnalysis = findViewById(R.id.catAnalysiIB);
        goToCaAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCaAnalysis = new Intent(CaTransactions.this, CaAnalysis.class);
                startActivity(goToCaAnalysis);
            }
        });
        // goto dashboard button
        goToDashboard = findViewById(R.id.dashboardIB);
        goToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDashboard = new Intent(CaTransactions.this, DashboardActivity.class);
                startActivity(goToDashboard);
            }
        });
    }

    // create transaction dialog
    public void createCaDialog() {
        caBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        View caAdView = getLayoutInflater().inflate(R.layout.ca_popup, null);

        caPayeeEditText = caAdView.findViewById(R.id.caPayeeET);
        caAmountEditText = caAdView.findViewById(R.id.caAmountET);
        caDorcSpinner = caAdView.findViewById(R.id.catDorcSpinnerXML);
        caTypeSpinner = caAdView.findViewById(R.id.catTypeSpinnerXML);
        caSaveButton = caAdView.findViewById(R.id.saveCaTButton);
        cancelPopupButton = caAdView.findViewById(R.id.cancelBXML);

        // debit/credit spinner setup
        List<String> dorcDropList = new ArrayList<>();
            dorcDropList.add("select credit or debit");
            dorcDropList.add("credit");
            dorcDropList.add("debit");
            dorcDropList.add("transfer to saving account");
        ArrayAdapter<String> dorcAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dorcDropList);
        dorcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caDorcSpinner.setAdapter(dorcAdapter);

        // populate drop down before dorc is selected
        List<String> typeDropList = new ArrayList<>();
            typeDropList.add("select type");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeDropList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caTypeSpinner.setAdapter(typeAdapter);

        // on select listener to dynamically change drop down lists
        caDorcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if (!(caDorcSpinner.getSelectedItem().toString() == "select credit or debit")) {
                   if (caDorcSpinner.getSelectedItem().toString() == "debit") {
                       debitTypeSpinner();
                   } else if(caDorcSpinner.getSelectedItem().toString() == "transfer to saving account") {
                       savingTypeSpinner();
                       caPayeeEditText.setText("Savings");
                   } else {
                       creditTypeSpinner();
                   }
               }
           }
           @Override
           public void onNothingSelected(AdapterView<?> parent) {
           }
       });
        // cancel button listener
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caDialog.dismiss();
            }
        });
        // save button listener
        caSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check input fields have values before calling save function
                if (caPayeeEditText.getText().toString().isEmpty() || caAmountEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please complete all fields", Toast.LENGTH_SHORT).show();
                } else if (caDorcSpinner.getSelectedItem().toString() == "select credit or debit or transfer") {
                    Toast.makeText(getApplicationContext(), "Please choose credit or debit", Toast.LENGTH_SHORT).show();
                } else if (caTypeSpinner.getSelectedItem().toString() == "choose type?") {
                    Toast.makeText(getApplicationContext(), "Please choose type", Toast.LENGTH_SHORT).show();
                } else {
                    saveCaTransaction();
                }
            }
        });
        // show add transaction dialog
        caBuilder.setView(caAdView);
        caDialog = caBuilder.create();
        caDialog.show();
    }

    // populate credit drop down
    public void creditTypeSpinner() {
        List<String> typeDropList = new ArrayList<>();
        typeDropList.add("wage");
        typeDropList.add("other income");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeDropList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caTypeSpinner.setAdapter(typeAdapter);
    }

    // populate saving drop down
    public void savingTypeSpinner() {
        List<String> typeDropList = new ArrayList<>();
        typeDropList.add("saving account");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeDropList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caTypeSpinner.setAdapter(typeAdapter);
    }

    // populate debit drop down
    public void debitTypeSpinner() {
        List<String> typeDropList = new ArrayList<>();
        typeDropList.add("household bill");
        typeDropList.add("food shopping");
        typeDropList.add("other shopping");
        typeDropList.add("entertainment");
        typeDropList.add("travel");
        typeDropList.add("miscellaneous");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeDropList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caTypeSpinner.setAdapter(typeAdapter);
    }

    // dialog save func
    private void saveCaTransaction() {
        // instantiate new cat db row
        CATransactions catTransNew = new CATransactions();
            String newCaAmount = caAmountEditText.getText().toString().trim();
            String newCaPayee = caPayeeEditText.getText().toString().trim();
            String newCaDorc = caDorcSpinner.getSelectedItem().toString().trim();
            String newCaType = caTypeSpinner.getSelectedItem().toString().trim();
            // add db data to row
            catTransNew.setAmount(newCaAmount);
            catTransNew.setDescription(newCaPayee);
            catTransNew.setCreditOrDebit(newCaDorc);
            catTransNew.setType(newCaType);
        // add data
        catDbHandler.addCAT(catTransNew);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                caDialog.dismiss();
                refreshRV();
            }
        }, 600);
    }

    // start/refresh recycler view
    public void refreshRV() {
        catTransList = new ArrayList<>();
        catTransList = catDbHandler.getAllCAT();
        Collections.reverse(catTransList);
        recyclerViewAdapter = new RVadapter(this, catTransList);
        rVCat.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
        Log.d("stu51272", "Recycler view refreshed ");
    }

    // set UI theme
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

