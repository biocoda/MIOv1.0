package com.biocoda.miov10;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class SmsActivity extends AppCompatActivity {

    private EditText msgToSendET;
    private Button sendButton, cancelMsgBT;
    private RadioButton addAccInfoRB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        msgToSendET = findViewById(R.id.smsMessageETXML);
        sendButton = findViewById(R.id.sendBTXML);
        cancelMsgBT = findViewById(R.id.msgCancelBTNXML);
        addAccInfoRB = findViewById(R.id.addInfoRBXML);

        cancelMsgBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDashboard = new Intent(SmsActivity.this, DashboardActivity.class);
                startActivity(goToDashboard);
            }
        });
        // start users own messaging app if they have one
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:"));
                smsIntent.putExtra("sms_body", msgToSendET.getText().toString());
                smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(smsIntent);
                }
            }
        });
    }

    public void radioButtonClicked(View view) {
        boolean isButtonChecked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.addInfoRBXML:
                String msgToAppend = msgToSendET.getText().toString();
                String accNoFromIntent = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                msgToSendET.setText(msgToAppend + accNoFromIntent);
                // reset button in case user wants to re-use
                final Handler splashHandler = new Handler();
                splashHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addAccInfoRB.setChecked(false);
                    }
                }, 1000);
                break;
            default:
                break;
        }
    }
}


