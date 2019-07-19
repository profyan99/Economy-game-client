package com.example.profy.gamecalculator.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.activity.transaction.SimpleTransactionActivity;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;

public class RegisterActivity extends SimpleTransactionActivity<KryoConfig.CompanyData> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.resourceTypeText)).setText("Компания");
        ((TextView) findViewById(R.id.textView6)).setText("ID карты:");
        ((TextView) findViewById(R.id.resourceText)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.resourceTitleText)).setText("Регистратура");
        ((Button) findViewById(R.id.resourceButton)).setText("Применить");

        receiver.addHandler(NetworkService.COMPANY_DATA_ACTION, Obj -> {
            updateEntities(((KryoConfig.CompanyDataListDto) Obj).companyDataList);
        });
    }

    @Override
    protected void sendData(KryoConfig.Identifier identifier) {
        KryoConfig.AddPlayer addPlayer = new KryoConfig.AddPlayer();
        addPlayer.company = currentEntity;
        identifier.plain = amount;
        addPlayer.identifier = identifier;
        networkService.sendData(addPlayer, this);
    }

    @Override
    protected String getDialogTitle() {
        return "Регистрация игровой nfc карты";
    }

    @Override
    protected void updateText() {
        //stub
    }


    @Override
    protected void retrieveEntities() {
        networkService.sendData(new KryoConfig.RequestCompanyDataListDto(), this);
    }

}
