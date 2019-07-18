package com.example.profy.gamecalculator.activity.bank;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;

public class VexelCashingActivity extends SimpleBankActivity {

    private final static String INFO_TEXT_DESC = "Игровой цикл: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.bankTitleText)).setText("Пункт обналичивания");
        ((Button) findViewById(R.id.bankButton)).setText("Обналичить");
        ((TextView) findViewById(R.id.textView6)).setText("ID векселя:");
        ((TextView) findViewById(R.id.hint_text_view))
                .setText("Не забудьте после обналичивания забрать вексель у игрока");
        descTextView.setText(INFO_TEXT_DESC);

        receiver.addHandler(NetworkService.CYCLE_ACTION, Obj -> {
            descTextView.setText(INFO_TEXT_DESC + ((KryoConfig.GameCycleDto) Obj).cycle);
        });
    }

    @Override
    protected String getDialogTitle() {
        return "Пункт обналичивания";
    }

    @Override
    protected void sendData(KryoConfig.Identifier identifier) {
        KryoConfig.VexelCashingDto vexelCashingDto = new KryoConfig.VexelCashingDto();
        vexelCashingDto.id = identifier;
        vexelCashingDto.vexelId = value;
        networkService.sendData(vexelCashingDto, this);
    }

    @Override
    protected void retrieveEntities() {
        networkService.sendData(new KryoConfig.RequestGameCycle(), this);
    }
}
