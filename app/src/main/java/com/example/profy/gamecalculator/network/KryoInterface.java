package com.example.profy.gamecalculator.network;

/**
 * Created by profy on 14.07.2018.
 */

public interface KryoInterface {

    void updateProducts(KryoConfig.ProductListDto productListDto);

    void updateResources(KryoConfig.ResourceListDto resourceListDto);

    void playerInformation(KryoConfig.PlayerInformation playerInformation);

    void transferStatus(KryoConfig.TransactionStatus transactionStatus);
}
