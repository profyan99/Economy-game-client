package com.example.profy.gamecalculator.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.List;
import java.util.Objects;

/**
 * Created by profy on 13.07.2018.
 */

public class KryoConfig {

    static final int SERVER_PORT = 54555;
    static final int SERVER_PORT_UDP = 54777;
    static final String ADDRESS = "194.190.163.136";//"192.168.43.232";


    static void register(EndPoint point) {
        Kryo kryo = point.getKryo();
        kryo.register(String.class);
        kryo.register(int[].class);
        kryo.register(double[].class);
        kryo.register(List.class);
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
    }

    public static class Entity {
        public int amount;
        public String name;

        public Entity(int amount, String name) {
            this.amount = amount;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class ResourceData extends Entity {

        public ResourceData(int cost, String name) {
            super(cost, name);
        }
    }

    public static class MoneyData extends Entity {

        public MoneyData(int amount, String name) {
            super(amount, name);
        }
    }

    public static class Identifier {
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
            if(byRFID) {
                return rfid;
            } else {
                return "" + plain;
            }
        }
    }


    public static class RequestResourceListDto {

    }

    public static class ResourceListDto {
        public List<ResourceData> resources;
    }

    public static class ResourceBuyDto {
        public Identifier id;
        public int amount;
        public ResourceData resource;
    }

    public static class ProductData extends Entity{

        public ProductData(int cost, String name) {
            super(cost, name);
        }
    }

    public static class RequestProductListDto {

    }

    public static class ProductListDto {
        public List<ProductData> products;
    }

    public static class ProductSellDto {
        public Identifier id;
        public int amount;
        public ProductData product;
    }

    public static class RequestPlayerInformation {
        public Identifier id;
    }

    public static class PlayerInformation {
        public String name;
        public int money;
        public List<ProductData> products;
        public List<ResourceData> resources;
    }

    public static class ProductTransferDto {
        public Identifier firstPlayer;
        public Identifier secondPlayer;
        public ProductData product;
        public int amount;
    }

    public static class ResourceTransferDto {
        public Identifier firstPlayer;
        public Identifier secondPlayer;
        public ResourceData resource;
        public int amount;
    }

    public static class MoneyTransferDto {
        public Identifier firstPlayer;
        public Identifier secondPlayer;
        public int amount;
    }

    public static class TransactionStatus {
        public boolean isSuccess;
        public String error;
    }

}
