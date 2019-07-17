package com.example.profy.gamecalculator.activity.transfer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;

public class MoneyTransferActivity extends SimpleTransferActivity<KryoConfig.MoneyData> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView) findViewById(R.id.transferTitleText)).setText("Трансфер средств");
        findViewById(R.id.transferTypeText).setVisibility(View.INVISIBLE);
        findViewById(R.id.transferSpinner).setVisibility(View.INVISIBLE);
        currentEntity = new KryoConfig.MoneyData(0, "");
    }

    @Override
    protected void retrieveEntities() {
        //stub
    }

    @Override
    protected void sendData() {
        Log.d( "Product transfer", "Sending data...");
        KryoConfig.MoneyTransferDto moneyTransferDto = new KryoConfig.MoneyTransferDto();
        moneyTransferDto.amount = amount;
        moneyTransferDto.firstPlayer = firstPerson;
        moneyTransferDto.secondPlayer = secondPerson;
        networkService.sendData(moneyTransferDto, this);
    }
}
