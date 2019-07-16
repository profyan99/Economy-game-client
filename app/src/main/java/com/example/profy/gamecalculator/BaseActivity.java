package com.example.profy.gamecalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.database.Cursor;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.profy.gamecalculator.network.KryoClient;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.KryoInterface;
import com.example.profy.gamecalculator.util.NfcManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class BaseActivity extends AppCompatActivity implements Serializable, KryoInterface {

    protected PendingIntent mPendingIntent;
    protected IntentFilter[] mFilters;
    protected String[][] mTechLists;
    protected NfcAdapter adapter;
    protected KryoClient kryoClient;
    protected AlertDialog currentAlertDialog;

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
            if(currentAlertDialog != null && currentAlertDialog.isShowing()) {
                runOnUiThread(() -> currentAlertDialog.cancel());
            }
            if(kryoClient != null) {
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

    /**
     * Called, when nfc was successfully read
     *
     * @param cardId 16 bytes nfc identifier
     */
    protected abstract void resolveNfc(String cardId);

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
                resolveNfc(cardId);
            } catch (IOException e) {
                Toast.makeText(this, "Error getting nfc data", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void showTransactionDialog(String title, Consumer<Integer> cardConsumer) {
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
                cardConsumer.accept(Integer.valueOf(editText.getText().toString()));
            }
        });
        builder.setNegativeButton("Отмена", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });

        currentAlertDialog = builder.create();
        currentAlertDialog.show();
    }


    @Override
    public void message(String message) {

    }

    @Override
    public void newCycle(KryoConfig.Prices newCycle) {

    }

    @Override
    public void corpAccount(KryoConfig.CorpAccount newCycle) {

    }

    @Override
    public void cargo(KryoConfig.PlayerInfo newCycle) {

    }

    @Override
    public void statusTransaction(KryoConfig.StatusTransaction newCycle) {

    }

    @Override
    public void statusMoneyTransfer(KryoConfig.StatusMoneyTransfer newCycle) {

    }

    @Override
    public void statusCargoTransfer(KryoConfig.StatusCargoTransfer newCycle) {

    }

    @Override
    public void updateProducts(KryoConfig.EntityListDto<KryoConfig.ProductData> productListDto) {

    }

    @Override
    public void updateResources(KryoConfig.EntityListDto<KryoConfig.ResourceData> resourceListDto) {

    }

    protected KryoConfig.Identifier getIdentifier(String id) {
        KryoConfig.Identifier identifier = new KryoConfig.Identifier();
        identifier.byRFID = true;
        identifier.rfid = id;
        return identifier;
    }

    protected KryoConfig.Identifier getIdentifier(int id) {
        KryoConfig.Identifier identifier = new KryoConfig.Identifier();
        identifier.byRFID = false;
        identifier.plain = id;
        return identifier;
    }
}
