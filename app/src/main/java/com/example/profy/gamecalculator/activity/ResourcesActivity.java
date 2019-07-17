package com.example.profy.gamecalculator.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;

import java.util.ArrayList;

public class ResourcesActivity extends SimpleTransactionActivity<KryoConfig.ResourceData> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView) findViewById(R.id.resourceTitleText)).setText("Станция покупки ресурсов");
        ((Button) findViewById(R.id.resourceButton)).setText("Купить");

        KryoConfig.EntityListDto<KryoConfig.ResourceData> res = new KryoConfig.EntityListDto<>();
        res.entities = new ArrayList<>();
        res.entities.add(new KryoConfig.ResourceData(10, "Wood"));
        res.entities.add(new KryoConfig.ResourceData(50, "Steal"));
        updateResources(res);
    }

    @Override
    protected void sendData(KryoConfig.Identifier identifier) {
        Log.d( "Resources", "Sending data...");
        KryoConfig.ResourceBuyDto resourceBuyDto = new KryoConfig.ResourceBuyDto();
        resourceBuyDto.amount = amount;
        resourceBuyDto.id = identifier;
        resourceBuyDto.resource = currentEntity;
        kryoClient.sendData(resourceBuyDto, this);
    }

    @Override
    protected void retrieveEntities() {
        kryoClient.sendData(new KryoConfig.RequestResourceListDto(), this);
    }

    @Override
    protected String getDialogTitle() {
        return "Покупка ресурсов";
    }

    @Override
    public void updateResources(KryoConfig.EntityListDto<KryoConfig.ResourceData> resourceListDto) {
        updateEntities(resourceListDto);
    }
}
