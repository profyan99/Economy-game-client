package com.example.profy.gamecalculator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profy.gamecalculator.network.KryoConfig;

public class ResourcesActivity extends BaseActivity {

    private int resourceAmount;
    private KryoConfig.Resource resourceType;
    private EditText resourceAmountEditText;
    private TextView resourceCostTextView;
    private int[] resourcesCosts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resourceAmountEditText = findViewById(R.id.resourceEdit);
        Spinner resourceTypeSpinner = findViewById(R.id.resourceSpinner);
        resourceCostTextView = findViewById(R.id.resourceText);

        ArrayAdapter<KryoConfig.Resource> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, KryoConfig.Resource.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resourceTypeSpinner.setAdapter(adapter);
        resourceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                resourceType = KryoConfig.Resource.values()[selectedItemPosition];
                resourceCostTextView.setText("Стоимость за одну штуку: " + resourcesCosts[resourceType.ordinal()]);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_resources;
    }

    @Override
    protected void resolveNfc(String cardId) {
        if (currentAlertDialog.isShowing()) {
            currentAlertDialog.cancel();
        }
        sendData(getIdentifier(cardId));
    }

    public void buyResource(View view) {
        if (resourceAmountEditText.getText().toString().isEmpty() || resourceType == null) {
            return;
        }
        resourceAmount = Integer.parseInt(resourceAmountEditText.getText().toString());
        showTransactionDialog("Покупка ресурсов", (cardId) -> {
            sendData(getIdentifier(cardId));
        });
    }

    private void sendData(KryoConfig.Identifier identifier) {
        Toast.makeText(this, "Sending data...", Toast.LENGTH_SHORT).show();
        KryoConfig.ResourceBuyDto resourceBuyDto = new KryoConfig.ResourceBuyDto();
        resourceBuyDto.amount = resourceAmount;
        resourceBuyDto.id = identifier;
        resourceBuyDto.resource = resourceType;
        kryoClient.sendData(resourceBuyDto);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void updateResourcesCosts(KryoConfig.ResourcesCostsDto costsDto) {
        resourcesCosts = costsDto.costs;
        if(resourceType != null) {
            resourceCostTextView.setText("Стоимость за одну штуку: " + resourcesCosts[resourceType.ordinal()]);
        }
    }
}
