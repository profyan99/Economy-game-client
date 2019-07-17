package com.example.profy.gamecalculator.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by profy on 13.07.2018.
 */

public class KryoConfig {

    static final int SERVER_PORT = 54555;
    static final int SERVER_PORT_UDP = 54777;
    static final String ADDRESS = "192.168.1.2";//"192.168.43.232";


    static void register(EndPoint point) {
        Kryo kryo = point.getKryo();
        kryo.register(String.class);
        kryo.register(int[].class);
        kryo.register(double[].class);
        kryo.register(List.class);
        kryo.register(ArrayList.class);
        kryo.register(Identifier.class);
        kryo.register(Entity.class);
        kryo.register(ResourceData.class);
        kryo.register(MoneyData.class);
        kryo.register(ProductData.class);
        kryo.register(RequestResourceListDto.class);
        kryo.register(ResourceListDto.class);
        kryo.register(ResourceBuyDto.class);
        kryo.register(RequestProductListDto.class);
        kryo.register(ProductListDto.class);
        kryo.register(ProductSellDto.class);
        kryo.register(RequestPlayerInformation.class);
        kryo.register(PlayerInformation.class);
        kryo.register(ProductTransferDto.class);
        kryo.register(ResourceTransferDto.class);
        kryo.register(MoneyTransferDto.class);
        kryo.register(TransactionStatus.class);
        kryo.register(RequestProductionListDto.class);
        kryo.register(ProductionDto.class);
    }

    public static class Entity implements Serializable {
        public int amount;
        public String name;

        public Entity(int amount, String name) {
            this.amount = amount;
            this.name = name;
        }

        public Entity() {
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class ResourceData extends Entity implements Serializable {

        public ResourceData(int cost, String name) {
            super(cost, name);
        }

        public ResourceData() {
        }


    }

    public static class MoneyData extends Entity implements Serializable {

        public MoneyData(int amount, String name) {
            super(amount, name);
        }

        public MoneyData() {
            super();
        }


    }

    public static class Identifier implements Serializable {
        public boolean byRFID;
        public String rfid = "";
        public int plain;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Identifier that = (Identifier) o;
            return byRFID == that.byRFID &&
                    plain == that.plain &&
                    Objects.equals(rfid, that.rfid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(byRFID, rfid, plain);
        }

        @Override
        public String toString() {
            if (byRFID) {
                return rfid;
            } else {
                return "" + plain;
            }
        }
    }


    public static class RequestResourceListDto implements Serializable {

    }

    public static class ResourceListDto implements Serializable {
        public List<ResourceData> resources;
    }

    public static class ResourceBuyDto implements Serializable {
        public Identifier id;
        public int amount;
        public ResourceData resource;
    }

    public static class ProductData extends Entity implements Serializable {

        public ProductData(int cost, String name) {
            super(cost, name);
        }

        public ProductData() {
        }
    }

    public static class RequestProductListDto implements Serializable {

    }

    public static class ProductListDto implements Serializable {
        public List<ProductData> products;
    }

    public static class ProductSellDto implements Serializable {
        public Identifier id;
        public int amount;
        public ProductData product;
    }

    public static class RequestPlayerInformation implements Serializable {
        public Identifier id;
    }

    public static class PlayerInformation implements Serializable {
        public String name;
        public int money;
        public List<ProductData> products;
        public List<ResourceData> resources;
    }

    public static class ProductTransferDto implements Serializable {
        public Identifier firstPlayer;
        public Identifier secondPlayer;
        public ProductData product;
        public int amount;
    }

    public static class ResourceTransferDto implements Serializable {
        public Identifier firstPlayer;
        public Identifier secondPlayer;
        public ResourceData resource;
        public int amount;
    }

    public static class MoneyTransferDto implements Serializable {
        public Identifier firstPlayer;
        public Identifier secondPlayer;
        public int amount;
    }

    public static class TransactionStatus implements Serializable {
        public boolean isSuccess;
        public String error;
    }

    public static class ProductionDto implements Serializable {
        public Identifier id;
        public ProductData product;
        public int amount;
    }

    public static class RequestProductionListDto implements Serializable {

    }

    public static class VexelCashingDto implements Serializable {
        public Identifier id;
        public int vexelId;
    }

    public static class RequestGameCycle implements Serializable {

    }

    public static class GameCycleDto implements Serializable {
        public int cycle;
    }

    public static class BankTransaction implements Serializable {
        public Identifier id;
        public int amount;
    }
}