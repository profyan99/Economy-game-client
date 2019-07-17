package com.example.profy.gamecalculator.activity.transfer;

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

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleTransferActivity<T extends KryoConfig.Entity> extends BaseActivity {
    protected T currentEntity;
    protected int amount;
    protected EditText amountEditText;
    protected TextView firstPersonView;
    protected TextView secondPersonView;
    protected List<T> entityData;
    protected ArrayAdapter<T> adapter;
    protected KryoConfig.Identifier firstPerson, secondPerson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        amountEditText = findViewById(R.id.transferEdit);
        firstPersonView = findViewById(R.id.transferView1);
        secondPersonView = findViewById(R.id.transferView2);
        Spinner resourceTypeSpinner = findViewById(R.id.transferSpinner);

        entityData = new ArrayList<>();

        retrieveEntities();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, entityData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resourceTypeSpinner.setAdapter(adapter);
        resourceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                currentEntity = entityData.get(selectedItemPosition);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    protected abstract void retrieveEntities();

    protected abstract void sendData();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_transfer;
    }

    public void resolveTransaction(View view) {
        if (amountEditText.getText().toString().isEmpty() || currentEntity == null
                || firstPerson == null || secondPerson == null) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show();
            return;
        }
        amount = Integer.parseInt(amountEditText.getText().toString());
        if (amount <= 0) {
            Toast.makeText(this, "Величина должна быть > 0", Toast.LENGTH_LONG).show();
            return;
        }
        sendData();
    }

    public void inputFirstPersonId(View view) {
        nfcHandler = cardId -> {
            nfcHandler = null;
            firstPerson = cardId;
            firstPersonView.setText("ID переводящего: " + firstPerson.toString());
        };
        showTransactionDialog("Идентификатор перепереводящего", nfcHandler);
    }

    public void inputSecondPersonId(View view) {
        nfcHandler = cardId -> {
            nfcHandler = null;
            secondPerson = cardId;
            secondPersonView.setText("ID переводящего: " + firstPerson.toString());
        };
        showTransactionDialog("Идентификатор получателя", nfcHandler);
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

        }
    }

    @Override
    public void transferStatus(KryoConfig.TransferStatus transferStatus) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder
                .append("Выполнено: \t")
                .append(transferStatus.isSuccess ? "Успешно" : "Ошибка")
                .append("\n");
        if(!transferStatus.isSuccess)  {
            contentBuilder
                    .append("Ошибка: \t")
                    .append(transferStatus.error);

        }
        showInformationDialog("Результат трансфера", contentBuilder.toString());
    }
}
