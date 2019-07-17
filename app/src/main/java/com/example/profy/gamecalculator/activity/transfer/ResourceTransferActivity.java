package com.example.profy.gamecalculator.activity.transfer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;

public class ResourceTransferActivity extends SimpleTransferActivity<KryoConfig.ResourceData> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView) findViewById(R.id.transferTitleText)).setText("Трансфер ресурсов");
    }

    @Override
    protected void retrieveEntities() {
        kryoClient.sendData(new KryoConfig.RequestResourceListDto(), this);
    }

    @Override
    protected void sendData() {
        Log.d( "Resource transfer", "Sending data...");
        KryoConfig.ResourceTransferDto resourceTransferDto = new KryoConfig.ResourceTransferDto();
        resourceTransferDto.amount = amount;
        resourceTransferDto.firstPlayer = firstPerson;
        resourceTransferDto.secondPlayer = secondPerson;
        resourceTransferDto.resource = currentEntity;
        kryoClient.sendData(resourceTransferDto, this);
    }

    @Override
    public void updateResources(KryoConfig.ResourceListDto resourceListDto) {
        updateEntities(resourceListDto.resources);
    }
}
