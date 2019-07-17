package com.example.profy.gamecalculator.util;

import com.example.profy.gamecalculator.activity.BaseActivity;
import com.example.profy.gamecalculator.network.KryoConfig;

@FunctionalInterface
public interface IdentificationAdapter {
    default void handle(String cardId) {
        handle(BaseActivity.getIdentifier(cardId));
    }

    default void handle(int cardId) {
        handle(BaseActivity.getIdentifier(cardId));
    }

    void handle(KryoConfig.Identifier identifier);
}
