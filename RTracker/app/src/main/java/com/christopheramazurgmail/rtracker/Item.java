package com.christopheramazurgmail.rtracker;


import java.io.Serializable;

/**
 * Created by haunter on 26/10/16.
 */
public class Item implements Serializable{
    private String desc;
    private double price;
    private String cat;

    public Item() {}
    public Item(String desc, double price) {
        this.desc = desc;
        this.price = price;
        this.cat = null;
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

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }
}
