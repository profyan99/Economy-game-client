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

import java.util.ArrayList;
import java.util.List;

public class ResourcesActivity extends BaseActivity {

    private int resourceAmount;
    private KryoConfig.ResourceData currentResource;
    private EditText resourceAmountEditText;
    private TextView resourceCostTextView;
    private List<KryoConfig.ResourceData> resourcesData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resourceAmountEditText = findViewById(R.id.resourceEdit);
        Spinner resourceTypeSpinner = findViewById(R.id.resourceSpinner);
        resourceCostTextView = findViewById(R.id.resourceText);
        resourcesData = new ArrayList<>();

        retrieveResources();

        ArrayAdapter<KryoConfig.ResourceData> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, resourcesData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resourceTypeSpinner.setAdapter(adapter);
        resourceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                currentResource = resourcesData.get(selectedItemPosition);
                resourceCostTextView.setText("Стоимость за одну штуку: " + currentResource.cost);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void retrieveResources() {
        kryoClient.sendData(new KryoConfig.RequestResourceListDto());
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
        if (resourceAmountEditText.getText().toString().isEmpty() || currentResource == null) {
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
        resourceBuyDto.resource = currentResource;
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
    public void updateResources(KryoConfig.ResourceListDto resourceListDto) {
        resourcesData = resourceListDto.resources;
        if (currentResource != null) {
            currentResource = resourcesData.stream()
                    .filter(res -> currentResource.name.equals(res.name))
                    .findFirst()
                    .orElse(currentResource);

            resourceCostTextView.setText("Стоимость за одну штуку: " + currentResource.cost);
        }
    }
}
