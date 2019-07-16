package com.example.profy.gamecalculator.network;

/**
 * Created by profy on 14.07.2018.
 */

public interface KryoInterface {
    void message(String message);

    void newCycle(KryoConfig.Prices newCycle);

    void corpAccount(KryoConfig.CorpAccount newCycle);

    void cargo(KryoConfig.PlayerInfo newCycle);

    void statusTransaction(KryoConfig.StatusTransaction newCycle);

    void statusMoneyTransfer(KryoConfig.StatusMoneyTransfer newCycle);

    void statusCargoTransfer(KryoConfig.StatusCargoTransfer newCycle);

    void updateProducts(KryoConfig.EntityListDto<KryoConfig.ProductData> productListDto);

    void updateResources(KryoConfig.EntityListDto<KryoConfig.ResourceData> resourceListDto);
}
