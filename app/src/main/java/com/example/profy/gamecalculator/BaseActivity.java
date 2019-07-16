package com.example.profy.gamecalculator;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.profy.gamecalculator.network.KryoClient;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.KryoInterface;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        //init client-server connection
        kryoClient = new KryoClient(this);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            kryoClient.stop();
        }));

        resolveIntent(getIntent());
    }

    /**
     * Set layout in activity
     * @return id of xml representation
     */
    protected abstract int getLayoutResourceId();

    /**
     * Called, when nfc was successfully read
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

    void resolveIntent(Intent intent) {
        if (Objects.equals(intent.getAction(), NfcAdapter.ACTION_TECH_DISCOVERED)) {
            try {
                String cardId = NfcManager.getNfcCardData(intent);
                Toast.makeText(this, cardId, Toast.LENGTH_LONG).show();
                resolveNfc(cardId);
            } catch (IOException e) {
                Toast.makeText(this, "Error getting nfc data", Toast.LENGTH_LONG).show();
            }
        }
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
}
