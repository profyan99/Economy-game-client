package com.example.profy.gamecalculator.activity.bank;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.activity.BaseActivity;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.util.IdentificationAdapter;

public abstract class SimpleBankActivity extends BaseActivity {
    protected TextView descTextView;
    protected EditText valueEditText;
    protected int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        descTextView = findViewById(R.id.bankText);
        valueEditText = findViewById(R.id.bankEdit);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_simple_bank;
    }

    public void resolveTransaction(View view) {
        if (valueEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show();
            return;
        }
        value = Integer.parseInt(valueEditText.getText().toString());
        IdentificationAdapter handler = cardId -> {
            nfcHandler = null;
            sendData(cardId);
        };
        nfcHandler = handler;
        showTransactionDialog(getDialogTitle(), handler);
    }

    protected abstract String getDialogTitle();

    protected abstract void sendData(KryoConfig.Identifier identifier);
}
