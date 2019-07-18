package com.example.profy.gamecalculator.activity.transfer;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;

public class ProductTransferActivity extends SimpleTransferActivity<KryoConfig.ProductData> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver.addHandler(NetworkService.PRODUCT_LIST_ACTION, Obj -> {
            updateEntities(((KryoConfig.ProductListDto) Obj).products);
        });

        ((TextView) findViewById(R.id.transferTitleText)).setText("Перевод товаров");
    }

    @Override
    protected void retrieveEntities() {
        networkService.sendData(new KryoConfig.RequestProductListDto(), this);
    }

    @Override
    protected void sendData() {
        Log.d( "Product transfer", "Sending data...");
        KryoConfig.ProductTransferDto productTransferDto = new KryoConfig.ProductTransferDto();
        productTransferDto.amount = amount;
        productTransferDto.firstPlayer = firstPerson;
        productTransferDto.secondPlayer = secondPerson;
        productTransferDto.product = currentEntity;
        networkService.sendData(productTransferDto, this);
    }
}
