package com.example.profy.gamecalculator.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

import static com.example.profy.gamecalculator.network.KryoConfig.SERVER_PORT_UDP;

/**
 * Created by profy on 13.07.2018.
 */

public class KryoClient {
    private Client client;


    public KryoClient(KryoInterface kryoInterface) {
        client = new Client();
        KryoConfig.register(client);
        client.start();

        client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                Log.d("kryo", "Connected");
                kryoInterface.message("Successfully connected");
            }

            @Override
            public void disconnected(Connection connection) {
                kryoInterface.message("Disconnected");
                Log.d("kryo", "Disconnected");
            }

            @Override
            public void received(Connection connection, Object object) {
                Log.d("kryo", "RECEIVED: " + object.toString() + " type:" + object.getClass().getCanonicalName());
                if (object instanceof KryoConfig.Prices) {
                    kryoInterface.newCycle((KryoConfig.Prices) object);
                } else if (object instanceof KryoConfig.CorpAccount) {
                    kryoInterface.corpAccount((KryoConfig.CorpAccount) object);
                } else if (object instanceof KryoConfig.PlayerInfo) {
                    kryoInterface.cargo((KryoConfig.PlayerInfo) object);
                } else if (object instanceof KryoConfig.StatusTransaction) {
                    kryoInterface.statusTransaction((KryoConfig.StatusTransaction) object);
                } else if (object instanceof KryoConfig.StatusMoneyTransfer) {
                    kryoInterface.statusMoneyTransfer((KryoConfig.StatusMoneyTransfer) object);
                } else if (object instanceof KryoConfig.StatusCargoTransfer) {
                    kryoInterface.statusCargoTransfer((KryoConfig.StatusCargoTransfer) object);

                } else {
                    Log.d("kryo", "Invalid Message type");
                }

            }

            @Override
            public void idle(Connection connection) {

            }
        }));
        new Thread(() -> {
            try {
                Log.d("kryo", "Connecting...");
                client.connect(5000, KryoConfig.ADDRESS, KryoConfig.SERVER_PORT, SERVER_PORT_UDP);
                Log.d("kryo", "Connected");
            } catch (IOException e) {
                Log.d("kryo", "Error: " + e.getMessage());
            }
        }).start();
    }

    public void stop() {
        Log.d("kryo", "Stopping client...");
        client.stop();
    }

    public void sendData(Object o, Context context) {
        Log.d("kryo", "Send data: " + o.toString());
        if(!client.isConnected()) {
            Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_LONG).show();
        } else {
            client.sendTCP(o);
            Toast.makeText(context, "Успешно", Toast.LENGTH_SHORT).show();
        }
    }


}
