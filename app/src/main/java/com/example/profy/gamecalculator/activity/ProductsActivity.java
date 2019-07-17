package com.example.profy.gamecalculator.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;

import java.util.ArrayList;

public class ProductsActivity extends SimpleTransactionActivity<KryoConfig.ProductData> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView) findViewById(R.id.resourceTitleText)).setText("Станция продажи товара");
        ((Button) findViewById(R.id.resourceButton)).setText("Продать");

        KryoConfig.EntityListDto<KryoConfig.ProductData> prods = new KryoConfig.EntityListDto<>();
        prods.entities = new ArrayList<>();
        prods.entities.add(new KryoConfig.ProductData(1000, "Plane"));
        prods.entities.add(new KryoConfig.ProductData(50, "Auto"));
        updateEntities(prods);
    }

    @Override
    protected void retrieveEntities() {
        kryoClient.sendData(new KryoConfig.RequestProductListDto(), this);
    }

    @Override
    protected void sendData(KryoConfig.Identifier identifier) {
        Log.d( "Products", "Sending data...");
        KryoConfig.ProductSellDto productSellDto = new KryoConfig.ProductSellDto();
        productSellDto.amount = amount;
        productSellDto.id = identifier;
        productSellDto.product = currentEntity;
        kryoClient.sendData(productSellDto, this);
    }

    @Override
    protected String getDialogTitle() {
        return "Продажа товара";
    }

    @Override
    public void updateProducts(KryoConfig.EntityListDto<KryoConfig.ProductData> productListDto) {
        updateEntities(productListDto);
    }
}
