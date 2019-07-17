package com.example.profy.gamecalculator.activity.bank;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;

public class TerminalActivity extends SimpleBankActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.bankTitleText)).setText("Банкомат");
        ((Button) findViewById(R.id.bankButton)).setText("Применить");
        descTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected String getDialogTitle() {
        return "Банкомат";
    }

    @Override
    protected void sendData(KryoConfig.Identifier identifier) {
        KryoConfig.BankTransaction bankTransaction = new KryoConfig.BankTransaction();
        bankTransaction.amount = value;
        bankTransaction.id = identifier;
        networkService.sendData(bankTransaction, this);
    }

    @Override
    protected void retrieveEntities() {
        //stub
    }
}
