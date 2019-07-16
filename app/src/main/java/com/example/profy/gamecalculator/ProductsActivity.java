package com.example.profy.gamecalculator;

import android.os.Bundle;
import android.view.View;

public class ProductsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_products;
    }

    @Override
    protected void resolveNfc(String cardId) {

    }

    public void sellProducts(View view) {
        
    }
}
