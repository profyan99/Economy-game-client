package com.example.profy.gamecalculator.activity.transaction;

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
import com.example.profy.gamecalculator.activity.BaseActivity;
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
    private static final String INFO_TEXT_DEST = "Стоимость за одну штуку: ";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amountEditText = findViewById(R.id.resourceEdit);
        Spinner resourceTypeSpinner = findViewById(R.id.resourceSpinner);
        costTextView = findViewById(R.id.resourceText);
        entityData = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, entityData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resourceTypeSpinner.setAdapter(adapter);
        resourceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                currentEntity = entityData.get(selectedItemPosition);
                updateText();
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

    protected void updateEntities(List<T> entityListDto) {
        entityData = entityListDto;
        adapter.clear();
        adapter.addAll(entityData);
        if (currentEntity != null) {
            currentEntity = entityData.stream()
                    .filter(res -> currentEntity.name.equals(res.name))
                    .findFirst()
                    .orElse(currentEntity);

            updateText();
        }
    }

    protected abstract void sendData(KryoConfig.Identifier identifier);

    protected abstract String getDialogTitle();

    protected void updateText() {
        costTextView.setText(INFO_TEXT_DEST + currentEntity.amount);
    }

}
