package com.example.profy.gamecalculator.activity.transaction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;

public class ProductsActivity extends SimpleTransactionActivity<KryoConfig.ProductData> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver.addHandler(NetworkService.PRODUCT_LIST_ACTION, Obj -> {
            updateEntities(((KryoConfig.ProductListDto) Obj).products);
        });

        ((TextView) findViewById(R.id.resourceTitleText)).setText("Станция продажи товара");
        ((Button) findViewById(R.id.resourceButton)).setText("Продать");
    }

    @Override
    protected void retrieveEntities() {
        networkService.sendData(new KryoConfig.RequestProductListDto(), this);
    }

    @Override
    protected void sendData(KryoConfig.Identifier identifier) {
        Log.d( "Products", "Sending data...");
        KryoConfig.ProductSellDto productSellDto = new KryoConfig.ProductSellDto();
        productSellDto.amount = amount;
        productSellDto.id = identifier;
        productSellDto.product = currentEntity;
        networkService.sendData(productSellDto, this);
    }

    @Override
    protected String getDialogTitle() {
        return "Продажа товара";
    }
}
