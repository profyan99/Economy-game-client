package com.example.profy.gamecalculator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.activity.bank.TerminalActivity;
import com.example.profy.gamecalculator.activity.bank.VexelCashingActivity;
import com.example.profy.gamecalculator.activity.transaction.ProductionActivity;
import com.example.profy.gamecalculator.activity.transaction.ProductsActivity;
import com.example.profy.gamecalculator.activity.transaction.ResourcesActivity;
import com.example.profy.gamecalculator.network.NetworkService;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements Serializable {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, NetworkService.class));
    }

    public void setResourceActivity(View view) {
        startActivity(new Intent(this, ResourcesActivity.class));
    }

    public void setProductActivity(View view) {
        startActivity(new Intent(this, ProductsActivity.class));
    }

    public void setProductionActivity(View view) {
        startActivity(new Intent(this, ProductionActivity.class));
    }

    public void setVexelCashingActivity(View view) {
        startActivity(new Intent(this, VexelCashingActivity.class));
    }

    public void setTerminalActivity(View view) {
        startActivity(new Intent(this, TerminalActivity.class));
    }

    public void setStateOrderActivity(View view) {
        startActivity(new Intent(this, StateOrderActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, NetworkService.class));
    }
}
