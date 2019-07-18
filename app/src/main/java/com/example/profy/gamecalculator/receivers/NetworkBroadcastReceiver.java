package com.example.profy.gamecalculator.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.profy.gamecalculator.network.NetworkService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.example.profy.gamecalculator.network.NetworkService.RETRIEVE_DATA;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = NetworkBroadcastReceiver.class.getName();
    private boolean active = true;

    Map<String, Consumer<Serializable>> handlers;

    public NetworkBroadcastReceiver() {
        handlers = new HashMap<>();
    }

    public void addHandler(String action, Consumer<Serializable> handler) {
        Log.i(LOG_TAG, "Add " + action + " handler");
        handlers.put(action, handler);
    }

    public void removeHandler(String action) {
        handlers.remove(action);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "received intent " + intent.getAction());
        if(active) {
            Serializable extra = null;
            if (intent.hasExtra(RETRIEVE_DATA)) {
                extra = intent.getSerializableExtra(RETRIEVE_DATA);
            }
            Consumer<Serializable> handler = handlers.get(intent.getAction());
            if(handler == null) {
                return;
            }
            handler.accept(extra);
        }
    }
}
