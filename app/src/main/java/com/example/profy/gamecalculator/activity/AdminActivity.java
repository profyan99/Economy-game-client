package com.example.profy.gamecalculator.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends BaseActivity {

    private CheckBox checkBox;
    private KryoConfig.ProductData type;
    private EditText amountEdit;
    private EditText costEdit;
    private List<KryoConfig.ProductData> entityData;
    private ArrayAdapter<KryoConfig.ProductData> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amountEdit = findViewById(R.id.adminEditAmount);
        costEdit = findViewById(R.id.adminEditCost);
        checkBox = findViewById(R.id.adminCheckVexel);
        Spinner resourceTypeSpinner = findViewById(R.id.adminSpinner);
        entityData = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, entityData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resourceTypeSpinner.setAdapter(adapter);
        resourceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                type = entityData.get(selectedItemPosition);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        receiver.addHandler(NetworkService.PRODUCT_LIST_ACTION, Obj -> {
            updateEntities(((KryoConfig.ProductListDto) Obj).products);
        });
    }

    protected void updateEntities(List<KryoConfig.ProductData> entityListDto) {
        entityData = entityListDto;
        adapter.clear();
        adapter.addAll(entityData);
        if (type != null) {
            type = entityData.stream()
                    .filter(res -> type.name.equals(res.name))
                    .findFirst()
                    .orElse(type);

        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_admin;
    }

    @Override
    protected void retrieveEntities() {
        networkService.sendData(new KryoConfig.RequestProductListDto(), this);
    }

    public void addStateOrder(View view) {
        if (amountEdit.getText().toString().isEmpty() || costEdit.getText().toString().isEmpty()
                || type == null) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show();
            return;
        }
        int amount = Integer.parseInt(amountEdit.getText().toString());
        int cost = Integer.parseInt(costEdit.getText().toString());

        networkService.sendData(new KryoConfig.AddStateOrderDto(
                cost,
                new KryoConfig.ProductData(amount, type.name),
                checkBox.isChecked()
        ), this);
    }

    public void newCycle(View view) {
        networkService.sendData(new KryoConfig.StartNewCycle(), this);
    }
}
