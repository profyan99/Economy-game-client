package com.example.profy.gamecalculator.activity.transaction;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;

public class ProductionActivity extends SimpleTransactionActivity<KryoConfig.ProductData> {

    private static final String TEXT_INFO_DESC = "Трудозатраты за шт: %d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView) findViewById(R.id.resourceTitleText)).setText("Производственный цех");
        ((Button) findViewById(R.id.resourceButton)).setText("Создать");
        costTextView.setText(String.format(TEXT_INFO_DESC, 0, 0));

        receiver.addHandler(NetworkService.PRODUCT_LIST_ACTION, Obj -> {
            updateEntities(((KryoConfig.ProductListDto) Obj).products);
        });


    }

    @Override
    protected void retrieveEntities() {
        networkService.sendData(new KryoConfig.RequestProductionListDto(), this);
    }

    @Override
    protected void sendData(KryoConfig.Identifier identifier) {
        Log.d("Production", "Sending data...");
        KryoConfig.ProductionDto productionDto = new KryoConfig.ProductionDto();
        productionDto.amount = amount;
        productionDto.id = identifier;
        productionDto.product = currentEntity;
        networkService.sendData(productionDto, this);
    }

    @Override
    protected String getDialogTitle() {
        return "Производство";
    }

    @Override
    protected void updateText() {
        costTextView.setText(String.format(TEXT_INFO_DESC, currentEntity.amount));
    }
}
