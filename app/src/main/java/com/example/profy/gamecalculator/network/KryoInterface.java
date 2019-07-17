package com.example.profy.gamecalculator.network;

/**
 * Created by profy on 14.07.2018.
 */

public interface KryoInterface {

    void updateProducts(KryoConfig.EntityListDto<KryoConfig.ProductData> productListDto);

    void updateResources(KryoConfig.EntityListDto<KryoConfig.ResourceData> resourceListDto);

    void playerInformation(KryoConfig.PlayerInformation playerInformation);

    void transferStatus(KryoConfig.TransferStatus transferStatus);
}
