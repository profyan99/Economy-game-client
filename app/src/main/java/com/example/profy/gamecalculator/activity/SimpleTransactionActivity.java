package com.example.profy.gamecalculator.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.util.IdentificationAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleTransactionActivity<T extends KryoConfig.Entity> extends BaseActivity {
    protected T currentEntity;
    protected int amount;
    protected EditText amountEditText;
    protected TextView costTextView;
    protected List<T> entityData;
    protected ArrayAdapter<T> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amountEditText = findViewById(R.id.resourceEdit);
        Spinner resourceTypeSpinner = findViewById(R.id.resourceSpinner);
        costTextView = findViewById(R.id.resourceText);
        entityData = new ArrayList<>();

        retrieveEntities();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, entityData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resourceTypeSpinner.setAdapter(adapter);
        resourceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                currentEntity = entityData.get(selectedItemPosition);
                costTextView.setText("Стоимость за одну штуку: " + currentEntity.amount);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void resolveTransaction(View view) {
        if (amountEditText.getText().toString().isEmpty() || currentEntity == null) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show();
            return;
        }
        amount = Integer.parseInt(amountEditText.getText().toString());
        if (amount <= 0) {
            Toast.makeText(this, "Величина должна быть > 0", Toast.LENGTH_LONG).show();
            return;
        }
        IdentificationAdapter handler = cardId -> {
            nfcHandler = null;
            sendData(cardId);
        };
        nfcHandler = handler;
        showTransactionDialog(getDialogTitle(), handler);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_resources;
    }

    protected void updateEntities(KryoConfig.EntityListDto<T> entityListDto) {
        entityData = entityListDto.entities;
        adapter.clear();
        adapter.addAll(entityData);
        if (currentEntity != null) {
            currentEntity = entityData.stream()
                    .filter(res -> currentEntity.name.equals(res.name))
                    .findFirst()
                    .orElse(currentEntity);

            costTextView.setText("Стоимость за одну штуку: " + currentEntity.amount);
        }
    }

    protected abstract void sendData(KryoConfig.Identifier identifier);

    protected abstract void retrieveEntities();

    protected abstract String getDialogTitle();


}
