package com.example.profy.gamecalculator.activity.transfer;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;

public class ProductTransferActivity extends SimpleTransferActivity<KryoConfig.ProductData> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView) findViewById(R.id.transferTitleText)).setText("Трансфер товаров");
    }

    @Override
    protected void retrieveEntities() {
        kryoClient.sendData(new KryoConfig.RequestProductListDto(), this);
    }

    @Override
    protected void sendData() {
        Log.d( "Product transfer", "Sending data...");
        KryoConfig.ProductTransferDto productTransferDto = new KryoConfig.ProductTransferDto();
        productTransferDto.amount = amount;
        productTransferDto.firstPlayer = firstPerson;
        productTransferDto.secondPlayer = secondPerson;
        productTransferDto.product = currentEntity;
        kryoClient.sendData(productTransferDto, this);
    }

    @Override
    public void updateProducts(KryoConfig.ProductListDto productListDto) {
        updateEntities(productListDto.products);
    }
}
