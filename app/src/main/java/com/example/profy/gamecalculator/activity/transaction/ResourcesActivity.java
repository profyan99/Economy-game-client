package com.example.profy.gamecalculator.activity.transaction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;

public class ResourcesActivity extends SimpleTransactionActivity<KryoConfig.ResourceData> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver.addHandler(NetworkService.RESOURCE_LIST_ACTION, Obj -> {
            updateEntities(((KryoConfig.ResourceListDto) Obj).resources);
        });
        ((TextView) findViewById(R.id.resourceTitleText)).setText("Покупка ресурсов");
        ((Button) findViewById(R.id.resourceButton)).setText("Купить");
    }

    @Override
    protected void sendData(KryoConfig.Identifier identifier) {
        Log.d( "Resources", "Sending data...");
        KryoConfig.ResourceBuyDto resourceBuyDto = new KryoConfig.ResourceBuyDto();
        resourceBuyDto.amount = amount;
        resourceBuyDto.id = identifier;
        resourceBuyDto.resource = currentEntity;
        networkService.sendData(resourceBuyDto, this);
    }

    @Override
    protected void retrieveEntities() {
        networkService.sendData(new KryoConfig.RequestResourceListDto(), this);
    }


    @Override
    protected String getDialogTitle() {
        return "Покупка ресурсов";
    }
}
