package com.example.profy.gamecalculator.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;
import com.example.profy.gamecalculator.receivers.NetworkBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public class ResourcesActivity extends SimpleTransactionActivity<KryoConfig.ResourceData> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver.addHandler(NetworkService.PRODUCT_LIST_ACTION, Obj -> {
            updateResources((KryoConfig.ResourceListDto) Obj);
        });
        ((TextView) findViewById(R.id.resourceTitleText)).setText("Станция покупки ресурсов");
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

    /*public void resolve(View view) {
        Log.i("ResourcesActivity", "sending broadcast with action " + NetworkService.PRODUCT_LIST_ACTION);
        Intent intent = new Intent();
        intent.setAction(NetworkService.PRODUCT_LIST_ACTION);
//        List<KryoConfig.ResourceData> resourceDataList = new ArrayList<>();
//        resourceDataList.add(new KryoConfig.ResourceData(100, "Svet off"));
//        intent.putExtra(NetworkService.RETRIEVE_DATA, new KryoConfig.ResourceListDto(resourceDataList));
        sendBroadcast(intent);
    }*/

    @Override
    protected String getDialogTitle() {
        return "Покупка ресурсов";
    }

    @Override
    public void updateResources(KryoConfig.ResourceListDto resourceListDto) {
        updateEntities(resourceListDto.resources);
    }
}
