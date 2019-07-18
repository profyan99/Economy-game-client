package com.example.profy.gamecalculator.activity.transfer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;

public class ResourceTransferActivity extends SimpleTransferActivity<KryoConfig.ResourceData> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver.addHandler(NetworkService.RESOURCE_LIST_ACTION, Obj -> {
            updateEntities(((KryoConfig.ResourceListDto) Obj).resources);
        });

        ((TextView) findViewById(R.id.transferTitleText)).setText("Перевод ресурсов");
    }

    @Override
    protected void retrieveEntities() {
        networkService.sendData(new KryoConfig.RequestResourceListDto(), this);
    }

    @Override
    protected void sendData() {
        Log.d( "Resource transfer", "Sending data...");
        KryoConfig.ResourceTransferDto resourceTransferDto = new KryoConfig.ResourceTransferDto();
        resourceTransferDto.amount = amount;
        resourceTransferDto.firstPlayer = firstPerson;
        resourceTransferDto.secondPlayer = secondPerson;
        resourceTransferDto.resource = currentEntity;
        networkService.sendData(resourceTransferDto, this);
    }

}
