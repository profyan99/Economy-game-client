package com.example.profy.gamecalculator.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import java.io.IOException;
import java.io.Serializable;

import static com.example.profy.gamecalculator.network.KryoConfig.SERVER_PORT;
import static com.example.profy.gamecalculator.network.KryoConfig.SERVER_PORT_UDP;

public class NetworkService extends Service {
    private Client client;

    public static final String PRODUCT_LIST_ACTION = "com.example.profy.gamecalculator.network.product";
    public static final String RESOURCE_LIST_ACTION = "com.example.profy.gamecalculator.network.resource";
    public static final String PLAYER_INFORMATION_ACTION = "com.example.profy.gamecalculator.network.player_information";
    public static final String TRANSACTION_STATUS_ACTION = "com.example.profy.gamecalculator.network.transaction_status";
    public static final String CYCLE_ACTION = "com.example.profy.gamecalculator.network.cycle";
    public static final String RETRIEVE_DATA = "data";

    private final NetworkBinder networkBinder = new NetworkBinder();

    public NetworkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return networkBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.set(Log.LEVEL_TRACE);
        client = new Client();
        KryoConfig.register(client);
        client.start();

        client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                Log.debug("Connected");
            }

            @Override
            public void disconnected(Connection connection) {
                Log.debug("Disconnected");
            }


            @Override
            public void received(Connection connection, Object object) {
                Log.debug("Received: " + object.toString());
                if (object instanceof KryoConfig.ProductListDto) {
                    Intent intent = new Intent(PRODUCT_LIST_ACTION);
                    intent.putExtra(RETRIEVE_DATA, (Serializable) object);
                    sendBroadcast(intent);
                } else if (object instanceof KryoConfig.ResourceListDto) {
                    Intent intent = new Intent(RESOURCE_LIST_ACTION);
                    intent.putExtra(RETRIEVE_DATA, (Serializable) object);
                    sendBroadcast(intent);
                } else if (object instanceof KryoConfig.PlayerInformation) {
                    Intent intent = new Intent(PLAYER_INFORMATION_ACTION);
                    intent.putExtra(RETRIEVE_DATA, (Serializable) object);
                    sendBroadcast(intent);
                } else if (object instanceof KryoConfig.TransactionStatus) {
                    Intent intent = new Intent(TRANSACTION_STATUS_ACTION);
                    intent.putExtra(RETRIEVE_DATA, (Serializable) object);
                    sendBroadcast(intent);
                } else if (object instanceof KryoConfig.GameCycleDto) {
                    Intent intent = new Intent(CYCLE_ACTION);
                    intent.putExtra(RETRIEVE_DATA, (Serializable) object);
                    sendBroadcast(intent);
                } else {
                    Log.debug("kryo", "Invalid Message type");
                }
            }

            @Override
            public void idle(Connection connection) {

            }
        }));

        ClientTask clientTask = new ClientTask(client);
        clientTask.execute();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (client != null) {
                client.stop();
            }
        }));
        return super.onStartCommand(intent, flags, startId);
    }

    public void sendData(Object o, Context context) {
        ClientSendTask clientSendTask = new ClientSendTask(client, context, o);
        clientSendTask.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (client != null && client.isConnected()) {
            client.stop();
            try {
                client.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ClientTask extends AsyncTask<Void, Void, Void> {
        private Client client;

        ClientTask(Client client) {
            this.client = client;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.debug("kryo", "Connecting...");
                client.connect(5000, KryoConfig.ADDRESS, SERVER_PORT, SERVER_PORT_UDP);
                Log.debug("kryo", "Connected");
            } catch (IOException e) {
                Log.debug("kryo", "Error: " + e.getMessage());
                cancel(false);
            }
            return null;
        }

    }

    static class ClientSendTask extends AsyncTask<Void, Void, Void> {
        private Client client;
        private Context context;
        private Object message;

        public ClientSendTask(Client client, Context context, Object message) {
            this.client = client;
            this.context = context;
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... objects) {
            if (!client.isConnected()) {
                cancel(false);
            } else {
                client.sendTCP(message);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(context, "Успешно", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_LONG).show();
        }
    }

    public class NetworkBinder extends Binder {
        public NetworkService getService() {
            return NetworkService.this;
        }
    }
}
