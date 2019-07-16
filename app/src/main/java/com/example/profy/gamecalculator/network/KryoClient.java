package com.example.profy.gamecalculator.network;

import android.util.Log;

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


    public KryoClient(KryoInterface kryoInterface, KryoConfig.PlanetNames planetName) {
        KryoInterface kryoInterface1 = kryoInterface;
        client = new Client();
        KryoConfig.register(client);
        client.start();

        //new ConnectToServer().execute();
        client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                Log.e("EE", "CONNECT");
                kryoInterface.message("Successfully connected");
            }

            @Override
            public void disconnected(Connection connection) {
                kryoInterface.message("Disconnected");
                Log.e("Log","Disconnect ---------------");
            }

            @Override
            public void received(Connection connection, Object object) {
                Log.e("EE", "RECEIVED: "+object.toString()+" type:"+object.getClass().getCanonicalName());
                if(object instanceof KryoConfig.Prices) {
                    kryoInterface.newCycle((KryoConfig.Prices)object);
                }
                else if(object instanceof KryoConfig.CorpAccount) {
                    kryoInterface.corpAccount((KryoConfig.CorpAccount)object);
                }
                else if(object instanceof KryoConfig.PlayerInfo) {
                    kryoInterface.cargo((KryoConfig.PlayerInfo)object);
                }
                else if(object instanceof KryoConfig.StatusTransaction) {
                    kryoInterface.statusTransaction((KryoConfig.StatusTransaction)object);
                }
                else if(object instanceof KryoConfig.StatusMoneyTransfer) {
                    kryoInterface.statusMoneyTransfer((KryoConfig.StatusMoneyTransfer)object);
                }
                else if(object instanceof KryoConfig.StatusCargoTransfer) {
                    kryoInterface.statusCargoTransfer((KryoConfig.StatusCargoTransfer)object);
                }
                else {
                    Log.e("Client", "Invalid Message type");
                }

            }

            @Override
            public void idle(Connection connection) {

            }
        }));
        new Thread(() -> {
            try {
                Log.e("Client", "Connecting...");
                client.connect(5000, KryoConfig.ADDRESS, KryoConfig.SERVER_PORT, SERVER_PORT_UDP);
                Log.e("Client", "Connected");
            } catch (IOException e) {
                Log.e("Client", "Error: " + e.getMessage());
            }
        }).start();
    }

    public void stop() {
        client.stop();
    }

    public void sendData(Object o) {
        Log.e("CLIENT", "|||||| SEND DATA: "+o.toString());
        client.sendTCP(o);
    }



}
