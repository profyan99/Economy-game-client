package com.example.profy.gamecalculator;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class ResourcesActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_resources;
    }

    @Override
    protected void resolveNfc(String cardId) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
