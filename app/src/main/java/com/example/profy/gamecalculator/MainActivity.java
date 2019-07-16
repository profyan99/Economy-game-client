package com.example.profy.gamecalculator;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profy.gamecalculator.network.KryoClient;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.KryoInterface;
import com.example.profy.gamecalculator.util.NfcManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.example.profy.gamecalculator.network.KryoConfig.CargoTransfer;
import static com.example.profy.gamecalculator.network.KryoConfig.CheckMoney;
import static com.example.profy.gamecalculator.network.KryoConfig.CheckPlayerInfo;
import static com.example.profy.gamecalculator.network.KryoConfig.Identifier;
import static com.example.profy.gamecalculator.network.KryoConfig.ItemType;
import static com.example.profy.gamecalculator.network.KryoConfig.MoneyTransfer;
import static com.example.profy.gamecalculator.network.KryoConfig.PlanetNames;
import static com.example.profy.gamecalculator.network.KryoConfig.Transaction;

public class MainActivity extends AppCompatActivity implements Serializable {

   /* double prices[][] = new double[ItemType.values().length][2];
    int quantities[] = new int[ItemType.values().length];

    EditText textFieldFirstId, textFieldSecondId;
    Button button;
    TextView text, dynText, planetNameText;
    PendingIntent mPendingIntent;
    IntentFilter[] mFilters;
    String[][] mTechLists;
    NfcAdapter adapter;
    KryoClient kryoClient;
    Spinner itemSpinner;

    PlanetNames planetName;
    List<String> planetValues = new ArrayList<>();
    List<String> itemsValues = new ArrayList<>();

    private enum OperationState {
        NONE, CHECK_MONEY, CHECK_CARGO, TRANSACTION, MONEY_TRANSFER, CARGO_TRANSFER
    }

    private class TransactionParams {
        ItemType type = null;
        int amount = -1;
        String rfids = "";

        public TransactionParams() {
            rfids = "";
        }
    }

    private class TransferParams {
        ItemType type = null;
        int amount = -1;
        String rfidFrom = "", rfidTo = "";
        boolean isMoneyTransfer = false;

        public TransferParams() {
            rfidFrom = "";
            rfidTo = "";
        }
    }

    private TransferParams transferParams;

    private TransactionParams transactionParams;

    private OperationState state = OperationState.NONE;
    private int cardFirstId = 0, cardSecondId = 0;
    private AlertDialog currentAlertDialog;

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && adapter.isEnabled()) {
            adapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.e("Foreground dispatch", "Discovered tag with intent: " + intent);
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
    public void onBackPressed() {

        if (currentAlertDialog.isShowing()) {
            state = OperationState.NONE;
            //currentAlertDialog.cancel();
        }
    }

    private void getPlanetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите планету"); // заголовок для диалога
        final String[] values = new String[planetValues.size()];
        planetValues.toArray(values);

        builder.setItems(values, (dialog, item) -> {
            Toast.makeText(getApplicationContext(),
                    "Выбранная планета: " + values[item],
                    Toast.LENGTH_SHORT).show();
            planetName = PlanetNames.values()[item];
            planetNameText.setText("Планета: " + planetName);
            sendPlanet();
            currentAlertDialog.cancel();
        });
        builder.setCancelable(false);
        currentAlertDialog = builder.create();
        currentAlertDialog.show();

    }

    private void getNFCEmptyDialog(boolean isNeedNFC) {
        if (isNeedNFC) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Запрос данных")
                    .setMessage("Поднестите NFC карту к телефону")
                    .setCancelable(false)
                    .setNegativeButton("", (dialogInterface, i) -> {

                    });
            builder.setCancelable(true);
            currentAlertDialog = builder.create();
            currentAlertDialog.show();
        } else {
            proceed("");
        }
    }

    private void getNFCTransferMoneyDialog(boolean needNfc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = getLayoutInflater();
        final View textEntryView = factory.inflate(R.layout.alert_transfer, null);

        builder
                .setView(textEntryView)
                .setCancelable(true)
                .setTitle("Трансфер средств");

        EditText editText = textEntryView.findViewById(R.id.amountTransfer);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        if (!needNfc) {
            builder.setNegativeButton("Ок", (dialogInterface, i) -> {
                if (editText.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Введите сначала значения", Toast.LENGTH_SHORT).show();
                } else {
                    transferParams.amount = Integer.parseInt(editText.getText().toString());
                    transferParams.rfidFrom = "";
                    transferParams.rfidTo = "";

                    moneyTransfer(
                            getIdentifier(cardFirstId, transferParams.rfidFrom),
                            getIdentifier(cardSecondId, transferParams.rfidTo),
                            transferParams.amount
                    );
                }
            });
        } else {
            builder.setNegativeButton("", (dialogInterface, i) -> {
            });
        }

        currentAlertDialog = builder.create();
        currentAlertDialog.show();
    }

    private void getNFCTransactionDialog(boolean needNfc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = getLayoutInflater();
        final View textEntryView = factory.inflate(R.layout.alert_transaction, null);
        builder
                .setView(textEntryView)
                .setCancelable(true);
        itemSpinner = textEntryView.findViewById(R.id.spinnerTrans);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);
        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                if (state == OperationState.TRANSACTION) {
                    transactionParams.type = ItemType.values()[selectedItemPosition];
                } else {
                    transferParams.type = ItemType.values()[selectedItemPosition];
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        EditText editText = textEntryView.findViewById(R.id.amount_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (!needNfc) {

            builder.setNegativeButton("Ок", (dialogInterface, i) -> {
                if (state != OperationState.TRANSACTION) {
                    if (editText.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Введите сначала значения, а потом поднесите карту получателя",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        transferParams.amount = Integer.parseInt(editText.getText().toString());
                        transferParams.rfidFrom = "";
                        transferParams.rfidTo = "";
                        cargoTransfer(
                                getIdentifier(cardFirstId, transferParams.rfidFrom),
                                getIdentifier(cardSecondId, transferParams.rfidTo),
                                transferParams.amount,
                                transferParams.type
                        );
                    }
                } else {
                    if (editText.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Введите сначала значения", Toast.LENGTH_SHORT).show();
                    } else {
                        transactionParams.amount = Integer.parseInt(editText.getText().toString());
                        transactionParams.rfids = "";
                        transaction(transactionParams.amount, transactionParams.type,
                                getIdentifier(cardFirstId, transactionParams.rfids));
                    }
                }
            });
        } else {
            builder.setNegativeButton("", (dialogInterface, i) -> {
            });
            if (state == OperationState.TRANSACTION) {
                builder.setTitle("Транзакция");
            } else {
                builder.setTitle("Трансфер ресурсов");
            }
        }


        currentAlertDialog = builder.create();
        currentAlertDialog.show();
    }

    private void getInfoDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Инфрмация")
                .setMessage(msg)
                .setCancelable(true)
                .setNegativeButton("Ок",
                        (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            ndef.addDataType("");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e("ERROR", "Intent filter error");
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[]{ndef};
        mTechLists = new String[][]{new String[]{MifareClassic.class.getName()}};


        kryoClient = new KryoClient(this);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            runOnUiThread(() -> currentAlertDialog.cancel());
            kryoClient.stop();
        }));


        textFieldFirstId = findViewById(R.id.editText);
        textFieldSecondId = findViewById(R.id.editText2);
        button = findViewById(R.id.button);
        text = findViewById(R.id.textView2);
        dynText = findViewById(R.id.textView);
        planetNameText = findViewById(R.id.planetName);

        planetNameText.setText("");
        textFieldFirstId.setText("0");
        textFieldSecondId.setText("0");

        resolveIntent(getIntent());

    }


    void updateDisplay() {
        StringBuilder titleStringBuilder = new StringBuilder();
        titleStringBuilder.append("Type\n\n\n");
        for (int i = 0; i < ItemType.values().length; i++) {
            titleStringBuilder.append(ItemType.values()[i].getS());
            titleStringBuilder.append("\n\n");
        }
        text.setText(titleStringBuilder);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Buy\t\t\t\tSell\t\t\t\tAmount\n\n\n");

        for (int i = 0; i < ItemType.values().length; i++) {
            stringBuilder.append(String.format(Locale.ENGLISH, "%.1f", prices[i][0]));
            stringBuilder.append("\t\t\t");
            stringBuilder.append(String.format(Locale.ENGLISH, "%.1f", prices[i][1]));
            stringBuilder.append("\t\t\t\t");
            stringBuilder.append(quantities[i]);
            stringBuilder.append("\n\n");
        }
        dynText.setText(stringBuilder);
    }

    void resolveIntent(Intent intent) {
        if (Objects.equals(intent.getAction(), NfcAdapter.ACTION_TECH_DISCOVERED)) {
            try {
                Toast.makeText(this, NfcManager.getNfcCardData(intent), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error getting nfc data", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void proceed(String id) {
        boolean success = true;
        switch (state) {
            case NONE: {
                break;
            }
            case CHECK_MONEY: {
                checkMoney(getIdentifier(cardFirstId, id));
                break;
            }
            case CHECK_CARGO: {
                checkPlayerInfo(getIdentifier(cardFirstId, id));
                break;
            }
            case TRANSACTION: {
                transactionParams.rfids = id;
                if (transactionParams.amount < 0) {
                    Toast.makeText(this, "Введите сначала значения", Toast.LENGTH_SHORT).show();
                    success = false;
                } else {
                    transaction(transactionParams.amount, transactionParams.type,
                            getIdentifier(cardFirstId, transactionParams.rfids));
                }
                break;
            }
            case MONEY_TRANSFER: {
                if (transferParams.rfidFrom.isEmpty()) {
                    transferParams.rfidFrom = id;
                    Toast.makeText(this, "Поднесите карту получателя", Toast.LENGTH_SHORT).show();
                    success = false;
                } else {
                    transferParams.rfidTo = id;
                    if (transferParams.amount <= 0) {
                        Toast.makeText(this, "Введите сначала значения", Toast.LENGTH_SHORT).show();
                        success = false;
                    } else {
                        moneyTransfer(
                                getIdentifier(cardFirstId, transferParams.rfidFrom),
                                getIdentifier(cardSecondId, transferParams.rfidTo),
                                transferParams.amount
                        );
                    }
                }
                break;
            }
            case CARGO_TRANSFER: {
                if (transferParams.rfidFrom.isEmpty()) {
                    transferParams.rfidFrom = id;
                    Toast.makeText(this, "Теперь поднесите карту получателя", Toast.LENGTH_SHORT).show();
                    success = false;
                } else {
                    transferParams.rfidTo = id;
                    if (transferParams.amount <= 0 || transferParams.type == null) {
                        Toast.makeText(this, "Введите сначала значения, а потом поднесите карту получателя",
                                Toast.LENGTH_SHORT).show();
                        success = false;
                    } else {
                        cargoTransfer(
                                getIdentifier(cardFirstId, transferParams.rfidFrom),
                                getIdentifier(cardSecondId, transferParams.rfidTo),
                                transferParams.amount,
                                transferParams.type
                        );
                    }
                }
                break;
            }
        }
        if (success) {
            Toast.makeText(this, "Success query: " + state, Toast.LENGTH_SHORT).show();
            state = OperationState.NONE;
            if (currentAlertDialog.isShowing()) {
                currentAlertDialog.cancel();
            }
            textFieldFirstId.setText("0");
            textFieldSecondId.setText("0");
        }
    }

    @Override
    public void message(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_SHORT).show());

    }

    @Override
    public void newCycle(KryoConfig.Prices prices) {
        for (int i = 0; i < prices.pricesToBuy.length; i++) {
            this.prices[i][0] = prices.pricesToSell[i];
            this.prices[i][1] = prices.pricesToBuy[i];
        }
        quantities = Arrays.copyOf(prices.quantities, prices.quantities.length);
        runOnUiThread(this::updateDisplay);

    }

    @Override
    public void corpAccount(KryoConfig.CorpAccount corpAccount) {
        String msg =
                "Корпорация:   " + corpAccount.name + "\n" + "Счет: " + Math.round(corpAccount.account) + "\n";

        runOnUiThread(() -> getInfoDialog(msg));
    }

    @Override
    public void cargo(KryoConfig.PlayerInfo cargo) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Имя: ")
                .append(cargo.name)
                .append("\nКорпорация: ")
                .append(cargo.corp)
                .append("\nСредства: ")
                .append(cargo.account)
                .append("\n\nРесурсы:\n");
        for (int i = 0; i < itemsValues.size(); i++) {
            stringBuilder.append(itemsValues.get(i)).append(":  ").append(cargo.cargo[i]).append("\n");
        }
        runOnUiThread(() -> getInfoDialog(stringBuilder.toString()));
    }

    @Override
    public void statusTransaction(KryoConfig.StatusTransaction statusTransaction) {
        runOnUiThread(() ->
                Toast.makeText(this, "Transaction: " + ((statusTransaction.success) ? ("Success") : ("Fail")),
                        Toast.LENGTH_SHORT).show());
    }

    @Override
    public void statusMoneyTransfer(KryoConfig.StatusMoneyTransfer statusMoneyTransfer) {
        runOnUiThread(() ->
                Toast.makeText(this, "Money transfer: " + ((statusMoneyTransfer.success) ? ("Success") : ("Fail")),
                        Toast.LENGTH_SHORT).show());
    }

    @Override
    public void statusCargoTransfer(KryoConfig.StatusCargoTransfer statusCargoTransfer) {
        runOnUiThread(() ->
                Toast.makeText(this, "Cargo transfer: " + ((statusCargoTransfer.success) ? ("Success") : ("Fail")),
                        Toast.LENGTH_SHORT).show());
    }

    private Identifier getIdentifier(int plain, String rfid) {
        Identifier id = new Identifier();
        if (id.byRFID = (plain <= 0)) {
            id.rfid = rfid;
        } else {
            id.plain = plain;
        }
        return id;
    }

    private boolean checkEditTextId() {
        cardFirstId = Integer.parseInt(textFieldFirstId.getText().toString());
        return cardFirstId <= 0;
    }

    private boolean checkEditTextTwoId() {
        cardSecondId = Integer.parseInt(textFieldSecondId.getText().toString());
        boolean first = checkEditTextId();
        return cardSecondId <= 0 && first;
    }

    // CLICK ----------------------------------------------------------------------------

    public void checkMoneyClick(View v) {
        state = OperationState.CHECK_MONEY;
        getNFCEmptyDialog(checkEditTextId());
    }

    public void checkCargoClick(View v) {
        state = OperationState.CHECK_CARGO;
        getNFCEmptyDialog(checkEditTextId());
    }

    public void transactionClick(View v) {
        boolean needNfc = checkEditTextId();
        if (needNfc) {
            Toast.makeText(this, "Введите значения, потом поднесите карту клиента", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Введите значения, потом нажмите ОК", Toast.LENGTH_LONG).show();
        }
        state = OperationState.TRANSACTION;
        transactionParams = new TransactionParams();
        getNFCTransactionDialog(needNfc);

    }

    public void moneyTransferClick(View view) {
        boolean needNfc = checkEditTextTwoId();
        if (needNfc) {
            Toast.makeText(this, "Введите значения, потом поднесите карту отправителя", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Введите значения, потом нажмите ОК", Toast.LENGTH_LONG).show();
        }
        state = OperationState.MONEY_TRANSFER;
        transferParams = new TransferParams();
        transferParams.isMoneyTransfer = true;
        getNFCTransferMoneyDialog(needNfc);
    }

    public void cargoTransferClick(View view) {
        boolean needNfc = checkEditTextTwoId();
        if (needNfc) {
            Toast.makeText(this, "Введите значения, потом поднесите карту отправителя", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Введите значения, потом нажмите ОК", Toast.LENGTH_LONG).show();
        }
        state = OperationState.CARGO_TRANSFER;
        transferParams = new TransferParams();
        getNFCTransactionDialog(needNfc);
    }

    private void checkMoney(Identifier id) {
        CheckMoney checkMoney = new KryoConfig.CheckMoney();
        checkMoney.id = id;
        new SandServer<>().execute(checkMoney);
    }

    private void transaction(int amount, ItemType itemType, Identifier id) {
        Transaction transaction = new Transaction();
        transaction.amount = amount;
        transaction.type = itemType;
        transaction.id = id;
        new SandServer<>().execute(transaction);
    }

    private void checkPlayerInfo(Identifier id) {
        CheckPlayerInfo CheckPlayerInfo = new CheckPlayerInfo();
        CheckPlayerInfo.id = id;
        new SandServer<>().execute(CheckPlayerInfo);
    }

    private void moneyTransfer(Identifier from, Identifier to, int amount) {
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.amount = amount;
        moneyTransfer.from = from;
        moneyTransfer.to = to;
        new SandServer<>().execute(moneyTransfer);

    }

    private void cargoTransfer(Identifier from, Identifier to, int quantity, ItemType type) {
        CargoTransfer cargoTransfer = new CargoTransfer();
        cargoTransfer.from = from;
        cargoTransfer.to = to;
        cargoTransfer.quantity = quantity;
        cargoTransfer.type = type;
        new SandServer<KryoConfig.CargoTransfer>().execute(cargoTransfer);
    }

    private void sendPlanet() {
        KryoConfig.PlanetInfo planetInfo = new KryoConfig.PlanetInfo();
        planetInfo.planet = planetName;
        new SandServer<KryoConfig.PlanetInfo>().execute(planetInfo);
    }

    class SandServer<T> extends AsyncTask<T, Void, Void> {
        @SafeVarargs
        @Override
        protected final Void doInBackground(T... arg0) {
            Log.e("SEND", "SENDING: " + arg0[0].toString());
            kryoClient.sendData(arg0[0]);
            return null;
        }

    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setResourceActivity(View view) {
        startActivity(new Intent(this, ResourcesActivity.class));
    }
}
