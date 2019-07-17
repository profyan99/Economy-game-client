package com.example.profy.gamecalculator.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.profy.gamecalculator.network.NetworkService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = NetworkBroadcastReceiver.class.getName();

    Map<String, Consumer<Serializable>> handlers;

    public NetworkBroadcastReceiver() {
        handlers = new HashMap<>();
    }

    public void addHandler(String action, Consumer<Serializable> handler) {
        handlers.put(action, handler);
    }

    public void removeHandler(String action) {
        handlers.remove(action);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "received intent " + intent.getAction());
        Serializable extra = intent.getSerializableExtra(NetworkService.RETRIEVE_DATA);
        Consumer<Serializable> handler = handlers.get(intent.getAction());

        handler.accept(extra);
    }
}
