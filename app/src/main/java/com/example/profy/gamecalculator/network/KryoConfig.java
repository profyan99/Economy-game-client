package com.example.profy.gamecalculator.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.List;

/**4
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
        kryo.register(PlanetNames.class);
        kryo.register(ItemType.class);
        kryo.register(Prices.class);
        kryo.register(Identifier.class);
        kryo.register(CheckMoney.class);
        kryo.register(CorpAccount.class);
        kryo.register(Transaction.class);
        kryo.register(StatusTransaction.class);
        kryo.register(PlayerInfo.class);
        kryo.register(CheckPlayerInfo.class);
        kryo.register(MoneyTransfer.class);
        kryo.register(StatusMoneyTransfer.class);
        kryo.register(CargoTransfer.class);
        kryo.register(StatusCargoTransfer.class);
        kryo.register(NewCycle.class);
        kryo.register(Register.class);
    }


    public static class Prices {
        public double[] pricesToSell;
        public double[] pricesToBuy;
        public int[] quantities;
    }

    public static class Entity {
        public int cost;
        public String name;

        public Entity(int cost, String name) {
            this.cost = cost;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class EntityListDto<T extends Entity> {
        public List<T> entities;
    }

    public static class ResourceData extends Entity {

        public ResourceData(int cost, String name) {
            super(cost, name);
        }
    }

    public static class Identifier {
        public boolean byRFID;
        public String rfid;
        public int plain;
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


    //OLD

    public static class CheckMoney {
        public Identifier id;
    }

    public static class CorpAccount {
        public String name;
        public double account;
    }


    public static class Transaction {
        public ItemType type;
        public int amount;
        public Identifier id;
    }

    public static class StatusTransaction {
        public boolean success;
    }

    public static class CheckPlayerInfo {
        public Identifier id;
    }

    public static class PlayerInfo {
        public String name;
        public String rfid;
        public int plain;
        public int[] cargo;
        public String corp;
        public double account;
    }

    public static class MoneyTransfer {
        public Identifier from;
        public Identifier to;
        public double amount;
    }

    public static class StatusMoneyTransfer {
        public boolean success;
    }

    public static class CargoTransfer {
        public Identifier from;
        public Identifier to;
        public int quantity;
        public ItemType type;
    }

    public static class StatusCargoTransfer {
        public boolean success;
    }

    public static class NewCycle {
    }

    public static class Register{
        PlayerInfo id;
    }

    public static class Save{
    }

    public enum ItemType {
        quantum_processors("Quantum Processors"),
        preserved_tachions("Preserved Tachions"),
        iridium("Iridium"),
        biomass("Biomass"),
        antimatter("Antimatter"),
        nanites("Nanites");
        private String s;

        ItemType(String s) {
            this.s = s;
        }

        public String getS() {
            return s;
        }
    }

    public enum PlanetNames {
        tarkek("Tarkek"),
        gleese("Gleese"),
        yanus("Yanus"),
        mimas("Mimas"),
        proximab("Proximab"),
        osiris("Osiris");
        private String s;

        PlanetNames(String s) {
            this.s = s;
        }

        public String getS() {
            return s;
        }
    }



}
