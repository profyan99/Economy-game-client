package com.example.profy.gamecalculator.activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.example.profy.gamecalculator.network.KryoClient;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.KryoInterface;
import com.example.profy.gamecalculator.util.IdentificationAdapter;
import com.example.profy.gamecalculator.util.NfcManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity implements Serializable, KryoInterface {

    protected PendingIntent mPendingIntent;
    protected IntentFilter[] mFilters;
    protected String[][] mTechLists;
    protected NfcAdapter adapter;
    protected KryoClient kryoClient;
    protected AlertDialog currentAlertDialog;
    protected IdentificationAdapter nfcHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        //NFC
        adapter = NfcAdapter.getDefaultAdapter(this);

        if (adapter == null) {
            Toast.makeText(this, "NFC not supported", Toast.LENGTH_LONG).show();
        }

        if (!adapter.isEnabled()) {
            Toast.makeText(this, "Enable NFC before using the app", Toast.LENGTH_LONG).show();
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

        //init client-server connection
        kryoClient = new KryoClient(this);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (currentAlertDialog != null && currentAlertDialog.isShowing()) {
                runOnUiThread(() -> currentAlertDialog.cancel());
            }
            if (kryoClient != null) {
                kryoClient.stop();
            }
        }));

        resolveIntent(getIntent());
    }

    /**
     * Set layout in activity
     *
     * @return id of xml representation
     */
    protected abstract int getLayoutResourceId();


    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && adapter.isEnabled()) {
            adapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d("Foreground dispatch", "Discovered tag with intent: " + intent);
        resolveIntent(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null && adapter.isEnabled()) {
            adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (kryoClient != null) {
            kryoClient.stop();
        }
    }

    void resolveIntent(Intent intent) {
        if (Objects.equals(intent.getAction(), NfcAdapter.ACTION_TECH_DISCOVERED)) {
            try {
                String cardId = NfcManager.getNfcCardData(intent);
                Toast.makeText(this, "Card id: " + cardId, Toast.LENGTH_LONG).show();
                if(nfcHandler != null) {
                    nfcHandler.handle(cardId);
                    if(currentAlertDialog != null && currentAlertDialog.isShowing()) {
                        currentAlertDialog.cancel();
                    }
                }
            } catch (IOException e) {
                Toast.makeText(this, "Error getting nfc data", Toast.LENGTH_LONG).show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
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

    @Override
    public void updateProducts(KryoConfig.EntityListDto<KryoConfig.ProductData> productListDto) {

    }

    @Override
    public void updateResources(KryoConfig.EntityListDto<KryoConfig.ResourceData> resourceListDto) {

    }

    @Override
    public void transferStatus(KryoConfig.TransferStatus transferStatus) {

    }

    @Override
    public void playerInformation(KryoConfig.PlayerInformation playerInformation) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder
                .append("Компания: \t\t")
                .append(playerInformation.name)
                .append("\n")
                .append("Счет: \t\t")
                .append(playerInformation.money)
                .append("\n\n")
                .append("-------------------------")
                .append("\n\n")
                .append("Товары: ")
                .append("\n");

        for (KryoConfig.ProductData product : playerInformation.products) {
            contentBuilder
                    .append("\t - ")
                    .append(product.name)
                    .append("\t")
                    .append(product.amount)
                    .append("\n");
        }
        contentBuilder
                .append("\n")
                .append("Ресурсы: ")
                .append("\n");

        for (KryoConfig.ResourceData resource : playerInformation.resources) {
            contentBuilder
                    .append("\t - ")
                    .append(resource.name)
                    .append("\t")
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
        kryoClient.sendData(requestPlayerInformation, this);
    }
}
