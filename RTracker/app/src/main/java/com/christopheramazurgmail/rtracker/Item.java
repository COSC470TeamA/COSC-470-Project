package com.christopheramazurgmail.rtracker;


/**
 * Created by haunter on 26/10/16.
 */
public class Item {
    private String desc;
    private double price;

    public Item() {}
    public Item(String desc, double price) {
        this.desc = desc;
        this.price = price;
    }
    public Item(String desc) {
        this.desc = desc;
        this.price = 0;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return String.format("%.2f", price);
    }
    public double getPriceD() { return price; }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "desc='" + desc + '\'' +
                ", price=" + price +
                '}';
    }
}
