package com.example.profy.gamecalculator.activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.activity.transfer.MoneyTransferActivity;
import com.example.profy.gamecalculator.activity.transfer.ProductTransferActivity;
import com.example.profy.gamecalculator.activity.transfer.ResourceTransferActivity;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;
import com.example.profy.gamecalculator.receivers.NetworkBroadcastReceiver;
import com.example.profy.gamecalculator.util.IdentificationAdapter;
import com.example.profy.gamecalculator.util.NfcManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class BaseActivity extends AppCompatActivity implements Serializable {

    protected PendingIntent mPendingIntent;
    protected IntentFilter[] mFilters;
    protected String[][] mTechLists;
    protected NfcAdapter adapter;
    protected AlertDialog currentAlertDialog;
    protected IdentificationAdapter nfcHandler;
    protected NetworkService networkService;
    private boolean mBound;
    protected NetworkBroadcastReceiver receiver;
    protected Toolbar toolbar;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, NetworkService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        receiver = new NetworkBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NetworkService.PRODUCT_LIST_ACTION);
        filter.addAction(NetworkService.PLAYER_INFORMATION_ACTION);
        filter.addAction(NetworkService.RESOURCE_LIST_ACTION);
        filter.addAction(NetworkService.TRANSACTION_STATUS_ACTION);
        filter.addAction(NetworkService.CYCLE_ACTION);
        filter.addAction(NetworkService.DISCONNECT_ACTION);
        filter.addAction(NetworkService.CONNECT_ACTION);
        filter.addAction(NetworkService.STATE_ORDERS_ACTION);
        filter.addAction(NetworkService.COMPANY_DATA_ACTION);
        filter.addAction(NetworkService.VEXEL_LIST_ACTION);
        registerReceiver(receiver, filter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Долг и честь");
        toolbar.setSubtitleTextColor(Color.RED);
        toolbar.setSubtitle("Не подключен");


        //NFC
        adapter = NfcAdapter.getDefaultAdapter(this);

        if (adapter == null) {
            Toast.makeText(this, "NFC не поддерживается", Toast.LENGTH_LONG).show();
        }

        if (!adapter.isEnabled()) {
            Toast.makeText(this, "Включите функцию NFC, прежде чем использовать",
                    Toast.LENGTH_LONG).show();
        }

        mPendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0
        );
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e("ERROR", "Intent filter error");
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[]{ndef};
        mTechLists = new String[][]{new String[]{MifareClassic.class.getName()}};

        receiver.addHandler(NetworkService.TRANSACTION_STATUS_ACTION, obj -> {
            transactionStatus((KryoConfig.TransactionStatus) obj);
        });

        receiver.addHandler(NetworkService.PLAYER_INFORMATION_ACTION, obj -> {
            playerInformation((KryoConfig.PlayerInformation) obj);
        });

        receiver.addHandler(NetworkService.DISCONNECT_ACTION, obj -> {
            toolbar.setSubtitleTextColor(Color.RED);
            toolbar.setSubtitle("Не подключен");
        });

        receiver.addHandler(NetworkService.CONNECT_ACTION, obj -> {
            toolbar.setSubtitleTextColor(Color.GREEN);
            toolbar.setSubtitle("Подключен");
        });

        resolveIntent(getIntent());
    }

    /**
     * Set layout in activity
     *
     * @return id of xml representation
     */
    protected abstract int getLayoutResourceId();

    protected abstract void retrieveEntities();


    @Override
    public void onResume() {
        super.onResume();
        receiver.setActive(true);
        if (adapter != null && adapter.isEnabled()) {
            adapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        resolveIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        receiver.setActive(false);
        if (adapter != null && adapter.isEnabled()) {
            adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    void resolveIntent(Intent intent) {
        if (Objects.equals(intent.getAction(), NfcAdapter.ACTION_TECH_DISCOVERED)) {
            try {
                String cardId = NfcManager.getNfcCardData(intent);
                Log.i("", "Card id: " + cardId);
                if (nfcHandler != null) {
                    nfcHandler.handle(cardId);
                    if (currentAlertDialog != null && currentAlertDialog.isShowing()) {
                        currentAlertDialog.cancel();
                    }
                }
            } catch (IOException e) {
                Toast.makeText(this, "Ошибка получения данных из nfc карты",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void showTransactionDialog(String title, IdentificationAdapter handler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = getLayoutInflater();
        final View textEntryView = factory.inflate(R.layout.alert_transaction, null);

        builder
                .setView(textEntryView)
                .setCancelable(true)
                .setTitle(title);

        EditText editText = textEntryView.findViewById(R.id.id_text);
        builder.setPositiveButton("Ок", (dialogInterface, i) -> {
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Введите сначала id карты\n или воспользуйтесь nfc",
                        Toast.LENGTH_SHORT).show();
            } else {
                handler.handle(Integer.valueOf(editText.getText().toString()));
            }
        });
        builder.setNegativeButton("Отмена", (dialogInterface, i) -> {
            nfcHandler = null;
            dialogInterface.cancel();
        });

        currentAlertDialog = builder.create();
        currentAlertDialog.show();
    }

    protected void showInformationDialog(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(content)
                .setCancelable(true)
                .setNegativeButton("Ок", (dialog, id) -> dialog.cancel());

        currentAlertDialog = builder.create();
        currentAlertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.account_information) {
            IdentificationAdapter handler = cardId -> {
                nfcHandler = null;
                sendRequestPlayerInformation(cardId);
            };
            nfcHandler = handler;
            showTransactionDialog("Информация об аккаунте", handler);
        } else if (id == R.id.transfer_money) {
            startActivity(new Intent(this, MoneyTransferActivity.class));
        } else if (id == R.id.transfer_resources) {
            startActivity(new Intent(this, ResourceTransferActivity.class));
        } else if (id == R.id.transfer_products) {
            startActivity(new Intent(this, ProductTransferActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void transactionStatus(KryoConfig.TransactionStatus transactionStatus) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder
                .append("Выполнено: \t")
                .append(transactionStatus.isSuccess ? "Успешно" : "Ошибка")
                .append("\n");
        if (!transactionStatus.isSuccess) {
            contentBuilder
                    .append("Ошибка: \t")
                    .append(transactionStatus.error);

        }
        showInformationDialog("Результат транзакции", contentBuilder.toString());
    }


    public void playerInformation(KryoConfig.PlayerInformation playerInformation) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder
                .append("Компания: \t\t")
                .append(playerInformation.name)
                .append("\n")
                .append("Счет: \t\t")
                .append(playerInformation.money)
                .append("\n")
                .append("Мощность производства: \t\t")
                .append(playerInformation.power)
                .append("\n\n")
                .append("--------------------------------------------------")
                .append("\n\n")
                .append("Товары: ")
                .append("\n\n");

        for (KryoConfig.ProductData product : playerInformation.products) {
            contentBuilder
                    .append("\t - ")
                    .append(product.name)
                    .append(": \t")
                    .append(product.amount)
                    .append("\n");
        }
        contentBuilder
                .append("\n")
                .append("Ресурсы: ")
                .append("\n\n");

        for (KryoConfig.ResourceData resource : playerInformation.resources) {
            contentBuilder
                    .append("\t - ")
                    .append(resource.name)
                    .append(": \t")
                    .append(resource.amount)
                    .append("\n");
        }
        showInformationDialog("Информация об аккаунте", contentBuilder.toString());
    }

    public static KryoConfig.Identifier getIdentifier(String id) {
        KryoConfig.Identifier identifier = new KryoConfig.Identifier();
        identifier.byRFID = true;
        identifier.rfid = id;
        return identifier;
    }

    public static KryoConfig.Identifier getIdentifier(int id) {
        KryoConfig.Identifier identifier = new KryoConfig.Identifier();
        identifier.byRFID = false;
        identifier.plain = id;
        return identifier;
    }

    private void sendRequestPlayerInformation(KryoConfig.Identifier identifier) {
        KryoConfig.RequestPlayerInformation requestPlayerInformation = new KryoConfig.RequestPlayerInformation();
        requestPlayerInformation.id = identifier;
        networkService.sendData(requestPlayerInformation, this);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            NetworkService.NetworkBinder binder = (NetworkService.NetworkBinder) service;
            networkService = binder.getService();
            mBound = true;
            if (networkService.isConnected()) {
                toolbar.setSubtitleTextColor(Color.GREEN);
                toolbar.setSubtitle("Подключен");
            } else {
                toolbar.setSubtitleTextColor(Color.RED);
                toolbar.setSubtitle("Не подключен");
            }
            retrieveEntities();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
